package github.ag777.util.lang;

import com.google.gson.JsonSyntaxException;
import github.ag777.util.gson.GsonUtils;
import github.ag777.util.lang.collection.ListUtils;
import github.ag777.util.lang.exception.ExceptionUtils;

import java.util.Collections;
import java.util.List;


/**
 * 控制台输出辅助类
 * <p>
 * 	目的是为了能在项目后期能方便地定位控制台输出代码的位置及控制其输出
 * </p>
 * 
 * @author ag777
 * @version last modify at 2020年05月20日
 */
public class Console {

	private static boolean devMode = true;
	private static boolean showSourceMethod = false;
	
	
	/**
	 * 控制是否打印日志, 默认为true
	 * @param devMode devMode
	 */
	public static void setDevMode(boolean devMode) {
		Console.devMode = devMode;
	}
	public static boolean isDevMode() {
		return devMode;
	}
	
	/**
	 * 控制是否在输出时显示调用源方法,默认为false
	 * @param showSourceMethod showSourceMethod
	 */
	public static void showSourceMethod(boolean showSourceMethod) {
		Console.showSourceMethod = showSourceMethod;
	}
	public static boolean showSourceMethod() {
		return showSourceMethod;
	}
	
	/**
	 * 控制台打印格式化信息
	 * @param obj obj
	 * @return
	 */
	public static String prettyLog(Object obj) {
		if(isDevMode()) {
			String msg = getMethod();
			
			if(obj != null && obj instanceof String) {
				msg += (String) obj;
			} else {
				msg += toJson(obj, true);
			}
			
			System.out.println(msg);
			return msg;
		} 
		return null;
	}
	
	/**
	 * 控制台打印信息
	 * @param obj obj
	 * @return 输出的日志内容
	 */
	public static String log(Object obj) {
		if(isDevMode()) {
			String msg = getMethod();
			
			if(obj instanceof String) {
				msg += (String) obj;
			} else {
				msg += toJson(obj, false);
			}
			
			System.out.println(msg);
			return msg;
		} 
		return null;
	}
	
	/**
	 * 将传入参数转为list并进行格式化输出
	 * 
	 * @param objs objs
	 * @return 输出日志的内容
	 */
	public static String prettyLog(Object obj, Object... objs) {
		if(isDevMode()) {
			if(objs == null) {
				return log(obj);
			}
			String msg = getMethod();
			List<Object> list = ListUtils.of(obj);
            Collections.addAll(list, objs);
			msg += toJson(list, true);
			System.out.println(msg);
			return msg;
		}
		return null;
	}
	
	/**
	 * 将传入参数转为list并进行输出
	 * @param objs objs
	 * @return 输出日志的内容
	 */
	public static String log(Object obj, Object... objs) {
		if(isDevMode()) {
			if(objs == null) {
				return log(obj);
			}
			String msg = getMethod();
			List<Object> list = ListUtils.of(obj);
            Collections.addAll(list, objs);
			msg += toJson(list, false);
			System.out.println(msg);
			return msg;
		}
		return null;
	}
	
	public static void err(String msg) {
		System.err.println(getMethod()+msg);
	}
	
	/**
	 * 打印错误栈信息(效果差不多等于)
	 * @param throwable throwable
	 */
	public static void err(Throwable throwable) {
		String msg = ExceptionUtils.getErrMsg(throwable);
		System.err.println(msg);
	}
	
	/*=================工具方法====================*/
	/**
	 * 获取调用该方法的信息
	 * @return 方法信息字符串
	 */
	private static String getMethod() {
		if(!showSourceMethod) {
			return "";
		}
		
		StackTraceElement[] stacks = (new Throwable()).getStackTrace();
		if(stacks.length > 0) {
			StackTraceElement stack = stacks[stacks.length-1];
			return new StringBuilder()
					.append(stack.getClassName())
					.append('【').append(stack.getMethodName()).append('】')
					.append(':').append(System.lineSeparator()).toString();
		} else {
			return "";
		}
		
		
	}
	
	/**
	 * 统一json转化
	 * <p>
	 *  先转json后格式化，效率会低一些，建议只在调试的时候用
	 * </p>
	 * @param obj obj
	 * @return json字符串
	 */
	private static String toJson(Object obj, boolean formatMode) {
		String json = GsonUtils.get().toJson(obj);
		if(formatMode) {	//需要被格式化
			try {
				json = GsonUtils.get().prettyFormat(json);
			} catch (JsonSyntaxException ignored) {
			}
		}
		return json;
	}
	
}