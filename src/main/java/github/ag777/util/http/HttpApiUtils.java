package github.ag777.util.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.ag777.util.file.FileUtils;
import github.ag777.util.gson.GsonUtils;
import github.ag777.util.http.model.MyCall;
import github.ag777.util.lang.IOUtils;
import github.ag777.util.lang.exception.model.GsonSyntaxException;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 第三方服务接口调用处理封装
 * @author ag777 <837915770@vip.qq.com>
 * @version  2024/04/19 14:48
 */
public class HttpApiUtils {

    /**
     * 发送请求并将响应结果转化为JsonObject
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return 返回JsonObject对象
     * @throws E 异常
     * @throws SocketTimeoutException socket超时\
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception>JsonObject executeForJsonObject(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        try {
            return GsonUtils.toJsonObjectWithException(json);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
    }

    /**
     * 发送请求并将响应结果转化为JsonArray
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return 返回JsonArray对象
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception>JsonArray executeForJsonArray(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        try {
            return GsonUtils.toJsonArrayWithException(json);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
    }

    /**
     * 发送请求并将响应结果转化为任意对象
     * @param call 请求
     * @param apiName 接口名
     * @param clazz 类型
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <T> 返回对象类型
     * @param <E> 抛出异常类型
     * @return 返回对象
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <T, E extends Exception>T executeForObj(MyCall call, String apiName, Class<T> clazz, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        T obj;
        try {
            obj = GsonUtils.get().fromJsonWithException(json, clazz);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
        return obj;
    }

    /**
     * 发送请求并保存响应列表
     * @param <E> 抛出异常类型
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @return 列表
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception> List<Map<String, Object>> executeForListMap(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        List<Map<String, Object>> resultMap;
        try {
            resultMap = GsonUtils.get().toListMapWithException(json);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
        return resultMap;
    }

    /**
     * 发送请求并保存响应列表
     * @param call 请求
     * @param apiName 接口名
     * @param clazzOfT 列表项的类型
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @param <T> 列表项的类型
     * @return 列表
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception, T> List<T> executeForList(MyCall call, String apiName, Class<T> clazzOfT, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        List<T> resultMap;
        try {
            resultMap = GsonUtils.get().toListWithException(json, clazzOfT);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
        return resultMap;
    }

    /**
     * 发送请求并保存响应Map
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return Map
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception>Map<String, Object> executeForMap(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        String json = executeForStr(call, apiName, toException, onHttpErr);
        Map<String, Object> resultMap;
        try {
            resultMap = GsonUtils.get().toMapWithException(json);
        } catch (GsonSyntaxException e) {
            throw toException.apply(apiName+"返回格式错误:"+json, e);
        }
        return resultMap;
    }

    /**
     * 发送请求并保存响应字符串
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return 字符串
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception>String executeForStr(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        Response res = executeForResponse(call, apiName, toException, onHttpErr);
        try {
            Optional<String> temp = HttpUtils.responseStrForce(res);
            if (temp.isEmpty()) {
                throw toException.apply(apiName+"返回为空", null);
            }
            return temp.get();
        } catch (IOException e) {
            throw toException.apply("解析"+apiName+"返回出现io异常", e);
        } finally {
            IOUtils.close(res);
        }

    }

    /**
     * 发送请求并保存响应文件
     * @param call 请求
     * @param apiName 接口名
     * @param targetPath 本地存储路径
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return 文件
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception> File executeForFile(MyCall call, String apiName, String targetPath, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        InputStream in = executeForInputStream(call, apiName, toException, onHttpErr);
        try {
            File file = FileUtils.write(in, targetPath);
            if(file.exists() && file.isFile()) {
                return file;
            }
            throw toException.apply("转写"+apiName+"返回异常,转写文件不存在", null);
        } catch (IOException e) {
            throw toException.apply("转写"+apiName+"返回出现io异常", e);
        }
    }

    /**
     * 发送请求并获取响应流
     * @param call 请求
     * @param apiName 接口名
     * @param toException 处理其它异常
     * @param onHttpErr 处理Http异常
     * @param <E> 抛出异常类型
     * @return 对端返回的输入流
     * @throws E 异常
     * @throws SocketTimeoutException socket超时
     * @throws ConnectException 连接异常
     */
    public static <E extends Exception>InputStream executeForInputStream(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        Response res = executeForResponse(call, apiName, toException, onHttpErr);
        try {
            Optional<InputStream> temp = HttpUtils.responseInputStream(res);
            if (temp.isEmpty()) {
                throw toException.apply(apiName+"返回为空", null);
            }
            return temp.get();
        } catch (IOException e) {
            throw toException.apply("读取"+apiName+"返回出现io异常", e);
        }
    }
    
    /**
     * 用于执行网络请求并处理异常的通用方法。
     *
     * @param call                  执行网络请求的具体调用对象。
     * @param apiName               当前调用的API名称，用于异常信息中。
     * @param toException           将IO异常转换为特定业务异常的方法。
     * @param onHttpErr             当HTTP请求失败时，用于转换为特定业务异常的方法。
     * @param <E>                   继承自Exception的特定异常类型。
     * @return                      返回网络请求的响应对象。
     * @throws E                    抛出由toException和onHttpErr转换的特定业务异常。
     * @throws SocketTimeoutException 抛出Socket超时异常。
     * @throws ConnectException      抛出连接异常。
     */
    public static <E extends Exception>Response executeForResponse(MyCall call, String apiName, BiFunction<String, Throwable, E> toException, Function<Response, E> onHttpErr) throws E, SocketTimeoutException, ConnectException {
        Response res;
        try {
            res = call.executeForResponse();  // 尝试执行网络请求。
        } catch(ConnectException | SocketTimeoutException e) {
            throw e; // 直接抛出连接和超时异常，不做处理。
        } catch (IOException e) {
            // 当出现IO异常时，转换为特定的业务异常。
            throw toException.apply(apiName+"调用失败", e);
        }
        // 检查响应是否成功，如果不成功则处理。
        if (!res.isSuccessful()) {
            if (onHttpErr != null) {
                // 使用onHttpErr转换HTTP错误为特定业务异常。
                E e = onHttpErr.apply(res);
                if (e != null) {
                    throw e;
                }
            }
            // 默认情况下，抛出API调用异常。
            throw toException.apply(apiName+"异常:"+res.code(), null);
        }
        return res; // 返回响应结果。
    }
}
