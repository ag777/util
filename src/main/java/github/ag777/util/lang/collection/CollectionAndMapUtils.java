package github.ag777.util.lang.collection;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


/**
 * 有关 <code>Collection和Map</code> 工具类。
 * 
 * @author ag777
 * @version create on 2017年06月15日,last modify at 2018年11月22日
 */
public class CollectionAndMapUtils {

	/**
	 * 创建数组
	 * <p>
	 * 	由于基本类型不能作为泛型，所以只好在外部自行强转了
	 * 	<p><pre>{@code
	 * 		CollectionAndMapUtils.newArray(int.class, 3) = [0,0,0];
	 * }</pre>
	 * 
	 * @param clazz 数组元素类型
	 * @param length 数组长度
	 * @return 创建的数组对象
	 */
	public static Object newArray(Class<?> clazz, int length) {
		return Array.newInstance(clazz, length);
	}
	
	/**
	 * 判断集合是否为空
	 * @param collection 要判断的集合
	 * @return true:集合为null或空; false:集合不为null且不为空
	 */
	public static <E>boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }
	
	/**
	 * 判断数组是否为空
	 * @param array 要判断的数组
	 * @return true:数组为null或长度为0; false:数组不为null且长度大于0
	 */
	public static <T>boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
	
	/**
	 * 判断Map是否为空
	 * @param map 要判断的Map
	 * @return true:Map为null或空; false:Map不为null且不为空
	 */
	public static <K,V>boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }
}