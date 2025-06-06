package github.ag777.util.http;

import github.ag777.util.file.FileUtils;
import github.ag777.util.gson.GsonUtils;
import github.ag777.util.http.model.MyCookieJar;
import github.ag777.util.http.model.ProgressResponseBody;
import github.ag777.util.http.model.SSLSocketClient;
import github.ag777.util.lang.ObjectUtils;
import github.ag777.util.lang.StringUtils;
import github.ag777.util.lang.collection.ArrayUtils;
import github.ag777.util.lang.collection.ListUtils;
import github.ag777.util.lang.collection.MapUtils;
import github.ag777.util.lang.exception.model.GsonSyntaxException;
import okhttp3.*;
import okhttp3.Request.Builder;

import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 有关HTTP请求的方法类（二次封装OkHttp3）
 * <p>
 * 需要的JAR包:
 * <ul>
 *     <li>okhttp-xxx.jar</li>
 *     <li>okio-xxx.jar</li>
 * </ul>
 *
 * <pre>{@code
 * 2017/6/8: 尝试通过反射机制参数callback<T>来转换结果为类，以达到优雅代码的目的，但未成功，原因如下:
 * 1. 直接用反射从参数中取泛型的类型，只实现了一个递归获取的方法（已删除）
 * 2. 通过Gson的TypeToken类来获取T的类型失败，原因可能是Java在编译时擦除了泛型类型
 * 2018/03/30: 重写
 * }</pre>
 *
 * 更新日志:
 * <ul>
 *     <li>OkHttp更新日志: <a href="https://github.com/square/okhttp/blob/master/CHANGELOG.md">OkHttp CHANGELOG</a></li>
 *     <li>Okio更新日志: <a href="https://github.com/square/okio/blob/master/CHANGELOG.md">Okio CHANGELOG</a></li>
 * </ul>
 *
 * @author ag777
 * @version 最后修改于 2024年12月05日
 */
public class HttpUtils {
	
	private static volatile OkHttpClient mOkHttpClient;

	public static final MediaType FORM_CONTENT_TYPE
			= MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//"Content-Type: application/json; charset=utf-8");//
	public static final MediaType JSON_CONTENT_TYPE
			= MediaType.parse("application/json; charset=utf-8");
	public static final MediaType TEXT_CONTENT_TYPE
			= MediaType.parse("text/plain");
	public static final MediaType OCTET_STREAM
			= MediaType.parse("application/octet-stream");
	private HttpUtils() {}
	
	/**
	 * 生成并获取client对象,双锁校验
	 * @return OkHttpClient
	 */
	public static OkHttpClient client() {
		if(mOkHttpClient == null) {
			synchronized (HttpUtils.class) {
				if(mOkHttpClient == null) {
					mOkHttpClient = defaultBuilder()  
		                    .build();  
				}
			}
		}
		return mOkHttpClient;
	}
	
	/**
	 * 默认builder
	 * <p>
	 * 连接超时时间为15秒,写出超时时间为15秒
	 * 绕过https验证
	 * </p>
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder defaultBuilder() {
		return new OkHttpClient().newBuilder()
				.connectTimeout(15, TimeUnit.SECONDS)
				.readTimeout(15, TimeUnit.SECONDS)  	//读取超时
				.writeTimeout(15, TimeUnit.SECONDS)
				// 不换第二个传输会报错: clientBuilder.sslSocketFactory(SSLSocketFactory) not supported on JDK 9+
				.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), (X509TrustManager) SSLSocketClient.getTrustManager()[0])
				.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
	}

	/**
	 * 定制读取超时时间
	 * @param builder builder
	 * @param timeout timeout
	 * @param unit unit
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder readTimeout(OkHttpClient.Builder builder, long timeout,  TimeUnit unit) {
		if(builder == null) {
			builder = defaultBuilder();
		}
		builder.readTimeout(timeout, unit);
		
		return builder;
	}
	
	/**
	 * 构建带拦截器的okhttpBuilder
	 * <p>
	 * 以下资料来源于:https://blog.csdn.net/briblue/article/details/52911998
	 * 不必关心url的重定向和重连。
	 只执行一次，即使Resopnse是来自于缓存。
	 只关心request的原始意图，而不用关心额外添加的Header信息如If-None-Match
	 * </p>
	 * 
	 * @param builder builder
	 * @param interceptors interceptors
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder builderWithInterceptor(OkHttpClient.Builder builder, Interceptor... interceptors) {
		if(builder == null) {
			builder = defaultBuilder();
		}
		
		if(interceptors != null) {
			for (Interceptor interceptor : interceptors) {
				builder.addInterceptor(interceptor);
			}
		}
		return builder;
	}
	
	/**
	 * 导入https证书
	 * @param builder builder
	 * @param certificates certificates
	 * @return OkHttpClient.Builder
	 * @throws KeyManagementException KeyManagementException
	 * @throws KeyStoreException KeyStoreException
	 * @throws CertificateException CertificateException
	 * @throws NoSuchAlgorithmException NoSuchAlgorithmException
	 * @throws IOException IOException
	 */
	@SuppressWarnings("deprecation")
	public static OkHttpClient.Builder builderWithHttpCertificate(OkHttpClient.Builder builder, InputStream... certificates) throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		if(builder == null) {
			builder = defaultBuilder();
		}
		
		builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(certificates));
		return builder;
	}
	
	/**
	 * 构建带网络拦截器的okhttpBuilder
	 * <p>
	 * 能够详尽地追踪访问链接的重定向。
	 短时间内的网络访问，它将不执行缓存过来的回应。
	 监测整个网络访问过程中的数据流向。
	 * </p>
	 * 
	 * @param builder builder
	 * @param interceptors interceptors
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder builderWithNetWorkInterceptor(OkHttpClient.Builder builder, Interceptor... interceptors) {
		if(builder == null) {
			builder = defaultBuilder();
		}
		
		if(interceptors != null) {
			for (Interceptor interceptor : interceptors) {
				builder.addNetworkInterceptor(interceptor);
			}
		}
		return builder;
	}
	
	/**
	 * 构造带cookie持久化的okhttpBuilder
	 * @param builder builder
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder builderWithCookie(OkHttpClient.Builder builder) {
		if(builder == null) {
			builder = client().newBuilder();
		}
		return builder.cookieJar(new MyCookieJar());
	}

	/**
	 * 构造带代理的okhttpBuilder
	 * @param builder builder
	 * @param ip ip
	 * @param port port
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder builderWithProxy(OkHttpClient.Builder builder, String ip, int port) {
		if(builder == null) {
			builder = client().newBuilder();
		}
		return builder.proxy(
				new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port))
		);
	}

	/**
	 * 构建带进度监听的okhttpBuilder
	 * @param builder builder
	 * @param listener listener
	 * @return OkHttpClient.Builder
	 */
	public static OkHttpClient.Builder builderWithProgress(OkHttpClient.Builder builder, ProgressResponseBody.ProgressListener listener) {
		if(builder == null) {
			builder = client().newBuilder();
		}
		if(listener != null) {
			return builder
					.addNetworkInterceptor(chain -> {
						Response response = chain.proceed(chain.request());
						//这里将ResponseBody包装成我们的ProgressResponseBody
						return response.newBuilder()
								.body(new ProgressResponseBody(response.body(),listener))
								.build();
					});
		}
		//监听事件和builder都为null则不重构client
		return builder;
	}
	
	/*===================GET请求===========================*/
	/**
	 * 取消所有请求
	 * @param clients 客户端
	 */
	public static void cancelAll(OkHttpClient... clients) {
		if (clients == null) {
			return;
		}
		for (OkHttpClient client : clients) {
			client.dispatcher().cancelAll();
		}
	}
	
	/**
	 * <p>
	 * 来源:https://www.zhihu.com/question/46147227
	 * </p>
	 * 
	 * @param client client
	 * @param tag tag
	 */
	public static void cancelAll(OkHttpClient client, Object tag) {
		if(tag == null) {
			cancelAll(client);
			return;
		}
		if(client != null) {
			Dispatcher dispatcher = client.dispatcher();
			synchronized (Dispatcher.class){
				for (Call call : dispatcher.queuedCalls()) {
					if (tag.equals(call.request().tag())) {
						call.cancel();
					}
				}
				for (Call call : dispatcher.runningCalls()) {
					if (tag.equals(call.request().tag())) {
						call.cancel();
					}
				}
			}
		}
	}
	
	/**
	 * get请求
	 * @param client client
	 * @param url url
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call getByClient(OkHttpClient client, String url, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		return getByClient(client, getGetUrl(url, paramMap), getHeaders(headerMap), tag);
	}
	
	/**
	 * get请求
	 * @param client client
	 * @param url url
	 * @param headers headers
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Call getByClient(OkHttpClient client, String url, Headers headers, Object tag) throws IllegalArgumentException {
		return call(
				getRequest(url, headers, tag).get().build(),
				client);
	}
	
	/*===================POST请求===========================*/
	
	/**
	 * post请求
	 * @param client client
	 * @param url url
	 * @param json json
	 * @param paramMap 放在url里的参数
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call postJsonByClient(OkHttpClient client, String url, String json, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		RequestBody requestBody = RequestBody.create(json, JSON_CONTENT_TYPE);
		return postByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}

	/**
	 * 发送 POST 请求，发送文本数据
	 *
	 * @param client OkHttpClient
	 * @param url 请求地址
	 * @param text 文本数据
	 * @param paramMap 放在url里的参数
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call postTextByClient(OkHttpClient client, String url, String text, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		RequestBody requestBody = RequestBody.create(text, TEXT_CONTENT_TYPE);
		return postByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}
	
	/**
	 * post请求
	 * @param client client
	 * @param url url
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call postByClient(OkHttpClient client, String url, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		return postByClient(client, url, getRequestBody(paramMap), getHeaders(headerMap), tag);
	}
	
	/**
	 * post请求
	 * @param client client
	 * @param url url
	 * @param body body
	 * @param headers headers
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Call postByClient(OkHttpClient client, String url, RequestBody body, Headers headers, Object tag) throws IllegalArgumentException {
		return call(
				getRequest(url, headers, tag).post(body).build(),
				client);
	}
	
	/*===================文件上传/下载===========================*/
	
	/**
	 * post请求带附件
	 * @param client client
	 * @param url url
	 * @param files files
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 * @throws FileNotFoundException FileNotFoundException
	 */
	public static <K, V>Call postMultiFilesByClient(OkHttpClient client, String url, String fileKey, File[] files, Map<K, V> paramMap, Map<K, V> headerMap, Object tag) throws IllegalArgumentException, FileNotFoundException {
		return postByClient(client, url, getRequestBody(fileKey, files, paramMap), getHeaders(headerMap), tag);
	}
	
	/**
	 * post请求带附件
	 * @param client client
	 * @param url url
	 * @param fileMap fileMap
	 * @param fileKey fileKey
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 * @throws FileNotFoundException FileNotFoundException
	 */
	public static <K, V>Call postMultiFilesByClient(OkHttpClient client, String url, Map<File, String> fileMap, String fileKey, Map<K, V> paramMap, Map<K, V> headerMap, Object tag) throws IllegalArgumentException, FileNotFoundException {
		return postByClient(client, url, getRequestBody(fileMap, fileKey, paramMap), getHeaders(headerMap), tag);
	}

	/*===================delete===========================*/

	/**
	 * delete请求
	 * @param client client
	 * @param url url
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call deleteByClient(OkHttpClient client, String url, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		return deleteByClient(client, getGetUrl(url, paramMap), null, getHeaders(headerMap), tag);
	}

	/**
	 * 使用指定的OkHttpClient删除JSON数据
	 *
	 * @param client OkHttpClient实例，用于发起网络请求
	 * @param url 请求的URL
	 * @param json 作为请求体的JSON字符串
	 * @param paramMap 请求参数键值对
	 * @param headerMap 请求头键值对
	 * @param tag 请求的标签，用于跟踪或取消请求
	 * @return 返回Call对象，可用于执行请求或进一步配置
	 * @throws IllegalArgumentException 如果参数不合法，抛出此异常
	 */
	public static <K,V>Call deleteJsonByClient(OkHttpClient client, String url, String json, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
	    RequestBody requestBody = RequestBody.create(json, JSON_CONTENT_TYPE);
	    return deleteByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}

	/**
	 * 使用指定的OkHttpClient删除文本数据
	 *
	 * @param client OkHttpClient实例，用于发起网络请求
	 * @param url 请求的URL
	 * @param json 作为请求体的文本字符串
	 * @param paramMap 请求参数键值对
	 * @param headerMap 请求头键值对
	 * @param tag 请求的标签，用于跟踪或取消请求
	 * @return 返回Call对象，可用于执行请求或进一步配置
	 * @throws IllegalArgumentException 如果参数不合法，抛出此异常
	 */
	public static <K,V>Call deleteTextByClient(OkHttpClient client, String url, String json, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
	    RequestBody requestBody = RequestBody.create(json, TEXT_CONTENT_TYPE);
	    return deleteByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}
	
	/**
	 * delete请求
	 * @param client client
	 * @param url url
	 * @param headers headers
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Call deleteByClient(OkHttpClient client, String url, RequestBody body, Headers headers, Object tag) throws IllegalArgumentException {
		return call(
				getRequest(url, headers, tag).delete(body).build(),
				client);
	}
	
	/*===================put===========================*/

	/**
	 * put请求
	 * @param client client
	 * @param url url
	 * @param json json
	 * @param paramMap 放在url里的参数
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call putJsonByClient(OkHttpClient client, String url, String json, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		RequestBody requestBody = RequestBody.create(json, JSON_CONTENT_TYPE);
		return putByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}

	/**
	 * put请求
	 *
	 * @param client OkHttpClient
	 * @param url 请求地址
	 * @param text 文本数据
	 * @param paramMap 放在url里的参数
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call putTextByClient(OkHttpClient client, String url, String text, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		RequestBody requestBody = RequestBody.create(text, TEXT_CONTENT_TYPE);
		return putByClient(client, getGetUrl(url, paramMap), requestBody, getHeaders(headerMap), tag);
	}

	/**
	 * put请求
	 * @param client client
	 * @param url url
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call putByClient(OkHttpClient client, String url, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		return putByClient(client, url, getRequestBody(paramMap), getHeaders(headerMap), tag);
	}
	
	/**
	 * put请求
	 * @param client client
	 * @param url url
	 * @param body body
	 * @param headers headers
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Call putByClient(OkHttpClient client, String url, RequestBody body, Headers headers, Object tag) throws IllegalArgumentException {
		return call(
				getRequest(url, headers, tag).put(body).build(),
				client);
	}
	
	/*===================head===========================*/
	
	/**
	 * head请求
	 * @param client client
	 * @param url url
	 * @param paramMap paramMap
	 * @param headerMap headerMap
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static <K,V>Call headByClient(OkHttpClient client, String url, Map<K, V> paramMap, Map<K,V> headerMap, Object tag) throws IllegalArgumentException {
		return headByClient(client, getGetUrl(url, paramMap), getHeaders(headerMap), tag);
	}
	
	/**
	 * delete请求
	 * @param client client
	 * @param url url
	 * @param headers headers
	 * @param tag tag
	 * @return Call
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Call headByClient(OkHttpClient client, String url, Headers headers, Object tag) throws IllegalArgumentException {
		return call(
				getRequest(url, headers, tag).head().build(),
				client);
	}
	
	/*===================其他方法===========================*/
	
	/**
	 * 发送请求并得到返回
	 * @param call call
	 * @return Response
	 * @throws SocketTimeoutException 一般为连不上接口
	 * @throws IOException 其他异常
	 */
	public static Response execute(Call call) throws SocketTimeoutException, IOException {
		return call.execute();
	}
	
	/**
	 * 从返回体重获取返回码
	 * @param response response
	 * @return Integer
	 */
	public static Integer responseCode(Response response) {
		if(response == null) {
			return null;
		}
		return response.code();
	}
	
	/**
	 * 从结果中获取字符串
	 * <p>
	 * 	只有response.isSuccessful()时才有返回,否则抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @return 返回字符串
	 * @throws IOException IOException
	 */
	public static Optional<String> responseStr(Response response) throws IOException{
		if(response == null) {
			return Optional.empty();
		}
		if(response.isSuccessful()) {
			return responseStrForce(response);
		}
		throw new IOException(response.code()+"||"+response.message());
	}
	
	/**
	 * 发送请求并得到返回字符串
	 * <p>
	 *  不论返回什么强制获取字符串
	 * </p>
	 * 
	 * @param response response
	 * @return 返回字符串
	 * @throws IOException IOException
	 */
	public static Optional<String> responseStrForce(Response response) throws IOException{
		if(response == null) {
			return Optional.empty();
		}
		if (response.body() == null) {
			return Optional.empty();
		}
		return Optional.of(response.body().string());
	}
	
	/**
	 * 发送请求并得到返回字符串
	 * <p>
	 * 	只有response.isSuccessful()时才有返回,否则抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @return 返回map
	 * @throws IOException IOException
	 */
	public static Optional<Map<String, Object>> responseMap(Response response) throws IOException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStr(response);
		return str.map(s -> GsonUtils.get().toMap(s));
	}
	
	/**
	 * 发送请求并得到返回字符串
	 * <p>
	 *  不论返回什么强制转化为map
	 * </p>
	 * 
	 * @param response response
	 * @return 返回map
	 * @throws IOException IOException
	 */
	public static Optional<Map<String, Object>> responseMapForce(Response response) throws IOException{
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStrForce(response);
		return str.map(s -> GsonUtils.get().toMap(s));
	}
	
	/**
	 * 发送请求并转为为javaBean
	 * <p>
	 * 只有response.isSuccessful()时才有返回,否则抛出异常
	 * 	转化失败会也会抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @param clazz clazz
	 * @return 返回任意类
	 * @throws IOException IOException
	 * @throws GsonSyntaxException json转化异常
	 */
	public static <T>Optional<T> responseObj(Response response, Class<T> clazz) throws IOException, GsonSyntaxException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStr(response);
		if(str.isPresent()) {
			return Optional.ofNullable(GsonUtils.get().fromJsonWithException(str.get(), clazz));
		}
		return Optional.empty();
	}
	
	/**
	 * 发送请求并转为为javaBean
	 * <p>
	 * 不论返回什么强制转化为对象
	 * 	转化失败会也会抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @param clazz clazz
	 * @return 返回任意类
	 * @throws IOException IOException
	 * @throws GsonSyntaxException json转化异常
	 */
	public static <T>Optional<T> responseObjForce(Response response, Class<T> clazz) throws IOException, GsonSyntaxException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStrForce(response);
		if(str.isPresent()) {
			return Optional.ofNullable(GsonUtils.get().fromJsonWithException(str.get(), clazz));
		}
		return Optional.empty();
	}
	
	/**
	 * 发送请求并转为为javaBean
	 * <p>
	 * 只有response.isSuccessful()时才有返回,否则抛出异常
	 * 	转化失败会也会抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @param type type
	 * @return 返回任意类
	 * @throws IOException IOException
	 * @throws GsonSyntaxException json转化异常
	 */
	public static <T>Optional<T> responseObj(Response response, Type type) throws IOException, GsonSyntaxException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStr(response);
		if(str.isPresent()) {
			return Optional.ofNullable(GsonUtils.get().fromJsonWithException(str.get(), type));
		}
		return Optional.empty();
	}
	
	/**
	 * 发送请求并转为为javaBean
	 * <p>
	 * 不论返回什么强制转化为对象
	 * 	转化失败会也会抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @param type type
	 * @return 返回任意类
	 * @throws IOException IOException
	 * @throws GsonSyntaxException json转化异常
	 */
	public static <T>Optional<T> responseObjForce(Response response, Type type) throws IOException, GsonSyntaxException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<String> str = responseStrForce(response);
		if(str.isPresent()) {
			return Optional.ofNullable(GsonUtils.get().fromJsonWithException(str.get(), type));
		}
		return Optional.empty();
	}
	
	/**
	 * 发送请求并得到返回流
	 * <p>
	 * 	只有response.isSuccessful()时才有返回,否则抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @return 返回输入流
	 * @throws IOException IOException
	 */
	public static Optional<InputStream> responseInputStream(Response response) throws IOException  {
		if(response == null) {
			return Optional.empty();
		}
		if(response.isSuccessful()) {
			if (response.body() == null) {
				return Optional.empty();
			}
			return Optional.of(response.body().byteStream());
		}
		throw new IOException(response.code()+"||"+response.message());
	}
	
	/**
	 * 发送请求，并将请求流保存成本地文件
	 * <p>
	 * 	只有response.isSuccessful()时才有返回,否则抛出异常
	 * </p>
	 * 
	 * @param response response
	 * @param targetPath targetPath
	 * @return 返回文件
	 * @throws IOException IOException
	 */
	public static Optional<File> responseFile(Response response, String targetPath) throws IOException {
		if(response == null) {
			return Optional.empty();
		}
		Optional<InputStream> in = responseInputStream(response);
		if(in.isPresent()) {
			File file = FileUtils.write(in.get(), targetPath);
			if(file.exists() && file.isFile()) {
				return Optional.of(file);
			}
		}
		return Optional.empty();
	}

	/**
	 *
	 * @param response response
	 * @return 响应头map
	 */
	public static Map<String, Object> responseHeaderMap(Response response) {
		Headers headers = response.headers();
		Set<String> names = headers.names();
		if (ListUtils.isEmpty(names)) {
			return Collections.emptyMap();
		}
		Map<String, Object> map = new LinkedHashMap<>(names.size());
		for (String name : names) {
			map.put(name, headers.get(name));
		}
		return map;
	}

	/**
	 * 构造请求头
	 * <p>
	 * 	只有response.isSuccessful()时才有返回,否则抛出异常
	 * </p>
	 * 
	 * @param headerMap headerMap
	 * @return Headers
	 */
	public static <K,V>Headers getHeaders(Map<K, V> headerMap) {
		if(headerMap == null || headerMap.isEmpty()) {
			return null;
		}
		Headers.Builder builder = new Headers.Builder();
		for (K key : headerMap.keySet()) {
			V value = headerMap.get(key);
			builder.add(key.toString(), value != null ? value.toString() : "");
		}
		return builder.build();
	}


	/**
	 * 请求并获取结果字符串(同步请求)
	 * @param request request
	 * @param client client
	 * @return Call
	 */
	public static Call call(Request request, OkHttpClient client) {
		if(client == null) {
			client = client();
		}
		return client.newCall(request);
	}


	/**
	 * 根据参数,请求头等数据构造request
	 * @param url url
	 * @param headers headers
	 * @param tag tag
	 * @return Builder
	 * @throws IllegalArgumentException 一般为url异常，比如没有http(s):\\的前缀
	 */
	public static Builder getRequest(String url, Headers headers, Object tag) throws IllegalArgumentException {
		Builder builder = new Builder()
				.url(url);

		if(headers != null) {
			builder.headers(headers);
		}

		if(tag != null) {
			builder.tag(tag);
		}
		return builder;
	}

	/**
	 *
	 * @param bytes 请求体内容(二进制)
	 * @param contentTypeStr 媒体类型
	 * @return 请求体
	 */
	public static RequestBody getRequestBody(byte[] bytes, String contentTypeStr) {
		MediaType mediaType = null;
		if (!StringUtils.isEmpty(contentTypeStr)) {
			mediaType = MediaType.parse(contentTypeStr);
		}
		return RequestBody.create(bytes, mediaType);
	}

	/**===================内部方法===========================*/

	/**
	 * 拼接get请求的url及参数
	 * @param url url
	 * @param params params
	 * @return url
	 */
	private static <K, V>String getGetUrl(String url, Map<K, V> params) {
		if(params == null || StringUtils.isBlank(url)) {
			return url;
		}
		String paramStr = getParamStr(params, false);	//get请求不错encode测试也没出现问题

		if(!paramStr.isEmpty()) {
			return url+"?"+paramStr;
		}
		return url;
	}



	/**
	 * 通过参数构建请求体
	 * <p>
	 * 注意:值为null的键值对不传输
	 * 不能用add方法，不然会中文乱码，目前只发现这种写法能解决
	 * </p>
	 *
	 * @param params params
	 * @return 请求体
	 */
	private static <K,V>RequestBody getRequestBody(Map<K, V> params) {
		String paramStr = getParamStr(params, true);
		if(!paramStr.isEmpty()) {
			return RequestBody.create(paramStr,  FORM_CONTENT_TYPE);
		}
		return  new FormBody.Builder().build();
	}

	/**
	 * 通过参数构建请求体
	 *
	 * <p>
	 * 	请事先对附件的存在性进行验证
	 * </p>
	 *
	 * @param fileKey 文件对应的key
	 * @param params params
	 * @return 请求体
	 * @throws FileNotFoundException FileNotFoundException
	 */
	private static <K,V>RequestBody getRequestBody(String fileKey, File[] files, Map<K, V> params) throws FileNotFoundException {
		Map<File, String> fileMap = null;
		/*附件部分*/
		if(!ArrayUtils.isEmpty(files)) {
			fileMap = new HashMap<>(files.length);
			for (File file : files) {
				fileMap.put(file, file.getName());
			}
		}

		return  getRequestBody(fileMap, fileKey, params);
	}

	/**
	 * 通过参数构建请求
	 * @param fileMap 文件及其上传名称对应map
	 * @param fileKey 上传文件对应的key
	 * @param params 其它参数
	 * @return 请求体
	 * @throws FileNotFoundException FileNotFoundException
	 */
	private static <K,V>RequestBody getRequestBody(Map<File, String> fileMap, String fileKey, Map<K, V> params) throws FileNotFoundException {
		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		/*附件部分*/
		addFiles2Form(builder, fileMap, fileKey);

		/*表单部分*/
		if(!MapUtils.isEmpty(params)) {
			for (K key : params.keySet()) {
				V value = params.get(key);
				builder.addFormDataPart(key.toString(), value == null ? "" : value.toString());
			}

			/*不能这么写，这样写jfinal只能通过getPara("params")获得到参数，还得自己解析
			 * if(!MapUtils.isEmpty(params)) {
				builder.addPart(Headers.of(
			            "Content-Disposition",
			            "form-data; name=\"params\""),
						getRequestBody(params));
			}*/
		}

		return  builder.build();
	}

	/**
	 * 将附件塞进请求体中
	 * @param builder 请求体构造器
	 * @param fileMap 文件及其上传名称对应map
	 * @param fileKey 上传文件对应的key
	 * @throws FileNotFoundException FileNotFoundException
	 */
	private static void addFiles2Form(MultipartBody.Builder builder, Map<File, String> fileMap, String fileKey) throws FileNotFoundException {
		if(!MapUtils.isEmpty(fileMap)) {
			for (File file : fileMap.keySet()) {
				if (file == null) {
					throw new FileNotFoundException(
							StringUtils.concat("文件上传失败:", "文件不能为空"));
				}
				if (!file.exists()) {
					throw new FileNotFoundException(
							StringUtils.concat("文件上传失败:", "文件[", file.getPath(), "]未找到"));
				}
				if (!file.isFile()) {
					throw new FileNotFoundException(
							StringUtils.concat("文件上传失败:", "文件[", file.getPath(), "]不是个文件"));
				}
				String fileName = fileMap.get(file);
				if (fileName == null) {
					fileName = file.getName();
				}
				RequestBody fileBody = RequestBody.create(file, OCTET_STREAM);
				builder.addFormDataPart(fileKey != null ? fileKey : "file", fileName, fileBody);
			}
		}
	}
	
	/**
	 * 
	 * @param params 参数表
	 * @return 拼接处的参数字符串
	 */
	private static <K, V> String getParamStr(Map<K, V> params, boolean needEncode) {
		if(params == null) {
			return "";
		}
		StringBuilder sb = null;
		for (K k : params.keySet()) {
			if (sb == null) {
				sb = new StringBuilder();
			} else {
				sb.append('&');
			}
			V value = params.get(k);
			if (value == null) {
				sb.append(k).append('=');
			} else if (ObjectUtils.isArray(value)) { // 数组
				int length = Array.getLength(value);
				for (int i = 0; i < length; i++) {
					Object item = Array.get(value, i);
					sb.append(k).append('=').append(encode(item, needEncode)).append('&');
				}
				sb.setLength(sb.length() - 1);    //不论列表是否为空都需要删除最后一个&号
			} else if (value instanceof Collection) { // 列表
				for (Object item : ((Collection<?>) value)) {
					sb.append(k).append('=').append(encode(item, needEncode)).append('&');
				}
				sb.setLength(sb.length() - 1);    //不论列表是否为空都需要删除最后一个&号
			} else { // 其余的通通转String,后续可加分支拓展
				sb.append(k).append('=').append(encode(value, needEncode));
			}
		}
		return StringUtils.emptyIfNull(sb);
	}
	
	/**
	 * @param value 值
	 * @param needEncode 是否需要encode操作
	 * @return urlEncode后的串(null会被置空)
	 */
	private static String encode(Object value, boolean needEncode) {
		if(value == null) {
			return "";
		}
		String v = value.toString();
		if(v.isEmpty()) {
			return "";
		}
		if(!needEncode) {
			return v;
		}
		try {	//application/x-www-form-urlencoded 模式下，需要对参数进行encode转义，否则接收方会丢失部分数据(比如&号)
			return URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			System.err.println("[HttpUtils]UnsupportedEncodingException:"+e.getMessage());
		}
		return v;
	}
	
}