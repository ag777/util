package github.ag777.util.gson.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 便捷地获取带泛型类的Type
 * <p>
 * 比如:
 * <pre>{@code
 * new TypeFactory(List.class, String.class) => List<String>
 * new TypeFactory(Map.class, String.class, Object.class) => Map<String, Object>
 * }</pre>
 * 
 * @author ag777
 * @version create on 2018年05月16日,last modify at 2019年04月22日
 */
public class TypeFactory implements ParameterizedType {

	/** 原始类型 */
	private final Type rawClass;
	/** 泛型参数类型数组 */
	private final Type[] argumentsType;

	/**
	 * 使用Class类型构造TypeFactory
	 * @param rawClass 原始类型
	 * @param argumentsClass 泛型参数类型
	 */
	public TypeFactory(Class<?> rawClass, Class<?>... argumentsClass) {
		this.rawClass = rawClass;
		this.argumentsType = argumentsClass;
	}
	
	/**
	 * 使用Type类型构造TypeFactory
	 * @param rawClass 原始类型
	 * @param argumentsType 泛型参数类型
	 */
	public TypeFactory(Class<?> rawClass, Type... argumentsType) {
		this.rawClass = rawClass;
		this.argumentsType = argumentsType;
	}

	/**
	 * 获取泛型参数类型数组
	 * @return 泛型参数类型数组
	 */
	@Override
	public Type[] getActualTypeArguments() {
		return argumentsType;
	}

	/**
	 * 获取所有者类型,本实现中不需要所有者类型,返回null
	 * @return null
	 */
	@Override
	public Type getOwnerType() {
		return null;
	}

	/**
	 * 获取原始类型
	 * @return 原始类型
	 */
	@Override
	public Type getRawType() {
		return rawClass;
	}
}
