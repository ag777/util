package github.ag777.util.lang.exception;

import github.ag777.util.gson.GsonUtils;
import github.ag777.util.lang.StringUtils;
import github.ag777.util.lang.collection.ArrayUtils;
import github.ag777.util.lang.collection.ListUtils;
import github.ag777.util.lang.collection.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * 统一的异常信息获取类
 * @author wanggz
 * Time: created at 2020/05/20. last modify at 2020/05/20.
 *
 */
public class ExceptionUtils {
	
	private ExceptionUtils() {}
	
	/**
	 * 获取异常的错误信息，以JSON字符串形式返回
	 * @param t 异常对象
	 * @return 包含异常信息的JSON字符串
	 */
	public static String getErrMsg(Throwable t) {
		return getErrMsg(t, null, null);
	}
	
	/**
	 * 获取指定工作包范围内的异常错误信息，以JSON字符串形式返回
	 * @param t 异常对象
	 * @param workingPackage 工作包名称
	 * @return 包含异常信息的JSON字符串
	 */
	public static String getErrMsg(Throwable t, String workingPackage) {
		return getErrMsg(t, workingPackage, null);
	}
	
	/**
	 * 获取指定工作包范围内的异常错误信息，可排除特定包，以JSON字符串形式返回
	 * @param t 异常对象
	 * @param workingPackage 工作包名称
	 * @param excludePackageList 需要排除的包名列表
	 * @return 包含异常信息的JSON字符串
	 */
	public static String getErrMsg(Throwable t, String workingPackage, List<String> excludePackageList) {
		Map<String, Object> errMap = getErrMap(t, workingPackage, excludePackageList);
		return GsonUtils.get().toJson(errMap);
	}
	
	/**
	 * 获取指定类中的异常错误信息，以JSON字符串形式返回
	 * @param throwable 异常对象
	 * @param clazz 指定的类
	 * @return 包含异常信息的JSON字符串
	 */
	public static String getErrMsg(Throwable throwable, Class<?> clazz) {
		Map<String, Object> errMap = getErrMap(throwable, clazz);
		return GsonUtils.get().toJson(errMap);
	}
	
	
	/**
	 * 获取异常的错误信息映射
	 * @param throwable 异常对象
	 * @return 包含异常信息的Map，key包含msg(异常信息)，可能包含line(行号)、method(方法名)、class(类名)和cause(原因)
	 */
	public static Map<String, Object> getErrMap(Throwable throwable) {
		return getErrMap(throwable, null, null);
	}
	
	/**
	 * 获取指定工作包范围内的异常错误信息映射
	 * @param throwable 异常对象
	 * @param workingPackage 工作包名称
	 * @return 包含异常信息的Map，key包含msg(异常信息)，可能包含line(行号)、method(方法名)、class(类名)和cause(原因)
	 */
	public static Map<String, Object> getErrMap(Throwable throwable, String workingPackage) {
		return getErrMap(throwable, workingPackage, null);
	}
	
	/**
	 * 从异常栈中提取异常信息，转换为Map形式
	 * @param throwable 异常对象
	 * @param workingPackage 工作包名称，用于限定异常栈的搜索范围
	 * @param excludePackageList 需要排除的包名列表
	 * @return 包含异常信息的Map，key包含msg(异常信息)，可能包含line(行号)、method(方法名)、class(类名)和cause(原因)
	 */
	public static Map<String, Object> getErrMap(Throwable throwable, String workingPackage, List<String> excludePackageList) {
		boolean hasWorkingPackage = !StringUtils.isEmpty(workingPackage);
		boolean hasExculdePackage = !ListUtils.isEmpty(excludePackageList);
		return getErrMap(throwable, (stackTraceElement)->{
			if(!hasWorkingPackage) {	//不包含工作路径返回第一个
				return true;
			}
			String clazzName = stackTraceElement.getClassName();	//类路径
			if(hasExculdePackage) {	//排除包
				for (String excludePackage : excludePackageList) {
					if(clazzName.startsWith(excludePackage)) {
						return false;
					}
				}
			}
			//在工作包下
			return clazzName.startsWith(workingPackage);
		});
	}
	
	/**
	 * 从异常栈中提取指定类的异常信息，转换为Map形式
	 * @param throwable 异常对象
	 * @param clazz 指定的类，会优先获取该类中产生的异常
	 * @return 包含异常信息的Map，key包含msg(异常信息)，可能包含line(行号)、method(方法名)、class(类名)和cause(原因)
	 */
	public static Map<String, Object> getErrMap(Throwable throwable, Class<?> clazz) {
		return getErrMap(throwable, (stackTraceElement)->{
			String clazzName = stackTraceElement.getClassName();	//类路径
			return clazzName.equals(clazz.getName());
		});
	}
	
	/**
	 * 从异常栈中提取满足条件的异常信息，转换为Map形式
	 * @param throwable 异常对象
	 * @param predicate 判断条件，返回true则提取对应栈信息并停止遍历(仅作用于最外层异常)
	 * @return 包含异常信息的Map，key包含msg(异常信息)，可能包含line(行号)、method(方法名)、class(类名)和cause(原因)
	 */
	public static Map<String, Object> getErrMap(Throwable throwable, java.util.function.Predicate<StackTraceElement> predicate) {
		if(throwable == null) {
			return null;
		}
		
		Map<String, Object> errMsg = MapUtils.of("msg", throwable.getMessage() != null?throwable.getMessage():throwable.toString());
		
		try {
			StackTraceElement[] s = throwable.getStackTrace();
			if(!ArrayUtils.isEmpty(s) ) {
				
				boolean findOne = false;
				for (StackTraceElement stackTraceElement : s) {
					if(predicate.test(stackTraceElement)) {
						findOne = true;
						String clazzName = stackTraceElement.getClassName();	//类路径
						int line = stackTraceElement.getLineNumber();			//异常相对于类所在行数
						String method = stackTraceElement.getMethodName();		//异常所属方法名
						errMsg.put("line", line);
						errMsg.put("method", method);
						errMsg.put("class", clazzName);
						break;
					}
				}
				if(!findOne) {	//如果前面没找到
					StackTraceElement s1 = s[0];
					String clazzName = s1.getClassName();	//类路径
					int line = s1.getLineNumber();			//异常相对于类所在行数
					String method = s1.getMethodName();		//异常所属方法名
					errMsg.put("line", line);
					errMsg.put("method", method);
					errMsg.put("class", clazzName);
				}
			}
			
			Throwable cause = throwable.getCause();
			if(cause != null) {
				errMsg.put("cause", getErrMap(cause, null, null));
			}
			
		} catch(Exception e) {
			//不处理
		}
		return errMsg;
	}
	
	/**
	 * 递归遍历异常栈，对每个栈帧执行判断操作
	 * @param throwable 异常对象
	 * @param predicate 判断条件，接收StackTraceElement(异常栈帧)和Integer(当前深度)参数，返回true时停止遍历
	 */
	public static void walk(Throwable throwable, java.util.function.BiPredicate<StackTraceElement, Integer> predicate) {
		walk(throwable, predicate, 0);
	}
	
	/**
	 * 递归遍历异常栈的内部实现方法
	 * @param throwable 异常对象
	 * @param predicate 判断条件，返回true时停止遍历
	 * @param deep 当前递归深度，每一个cause深度加1
	 */
	private static void walk(Throwable throwable, java.util.function.BiPredicate<StackTraceElement, Integer> predicate, int deep) {
		if(throwable == null) {
			return;
		}
		
		StackTraceElement[] s = throwable.getStackTrace();
		if(!ArrayUtils.isEmpty(s) ) {
			for (StackTraceElement stackTraceElement : s) {
				if(predicate.test(stackTraceElement, deep)) {
					return;
				}
			}
		}
		
		Throwable cause = throwable.getCause();
		if(cause != null) {
			walk(throwable, predicate, deep+1);
		}
		
	}

}
