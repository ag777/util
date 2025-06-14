package github.ag777.util.lang.collection;

import github.ag777.util.lang.ObjectUtils;
import github.ag777.util.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * 有关 <code>Map</code> 哈希表工具类。
 * 
 * @author ag777
 * @version create on 2017年09月22日,last modify at 2025年06月14日
 */
public class MapUtils {
	
	/**
	 * 判断Map是否为空
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 待检查的Map
	 * @return true：Map为null或空，false：Map不为空
	 */
	public static <K, V>boolean isEmpty(Map<K, V> map) {
		return CollectionAndMapUtils.isEmpty(map);
	}
	
	/**
	 * 创建一个新的Map并将给定Map的所有键值对复制到新Map中
	 * 
	 * 此方法通过创建新的HashMap并执行putAll操作来实现Map的深度拷贝。
	 * 新Map的初始容量将设置为源Map的大小，以优化性能。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @return 包含源Map所有键值对的新HashMap实例
	 */
	public static <K, V>Map<K, V> of(Map<K, V> map) {
		HashMap<K, V> result = new HashMap<>(map.size());
		putAll(result, map);
		return result;
	}
	
	/**
	 * 构建包含单个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加指定的键值对。
	 * 这是一个便捷方法，用于快速创建只包含一个条目的Map。
	 * 
	 * @param key 要添加的键
	 * @param value 要添加的值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key, Object value) {
		return of(String.class, Object.class, key, value);
	}
	
	/**
	 * 构建包含单个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key 要添加的键
	 * @param value 要添加的值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key, V value) {
		Map<K, V> map = new HashMap<>(10);
		map.put(key, value);
		return map;
	}

	/**
	 * 构建包含两个键值对的Map
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2) {
		return of(String.class, Object.class, key1, value1, key2, value2);
	}
	
	/**
	 * 构建包含两个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加两个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1);
		map.put(key2, value2);
		return map;
	}

	/**
	 * 构建包含三个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加三个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3);
	}

	/**
	 * 构建包含三个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加三个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}
	
	/**
	 * 构建包含四个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加四个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4);
	}
	
	/**
	 * 构建包含四个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加四个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}
	
	/**
	 * 构建包含五个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加五个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5);
	}
	
	/**
	 * 构建包含五个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加五个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}
	
	/**
	 * 构建包含六个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加六个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6);
	}
	
	/**
	 * 构建包含六个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加六个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5);
		map.put(key6, value6);
		return map;
	}
	
	/**
	 * 构建包含七个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加七个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7);
	}
	
	/**
	 * 构建包含七个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加七个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6);
		map.put(key7, value7);
		return map;
	}
	
	/**
	 * 构建包含八个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加八个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键	
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, key8, value8);
	}
	
	/**
	 * 构建包含八个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加八个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7);
		map.put(key8, value8);
		return map;
	}
	
	/**
	 * 构建包含九个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加九个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @param key9 第九个键
	 * @param value9 第九个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, key8, value8, key9, value9);
	}
	
	/**
	 * 构建包含九个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加九个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @param key9 第九个键
	 * @param value9 第九个值
	 * @return 包含指定键值对的新Map实例
	 */
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, key8, value8);
		map.put(key9, value9);
		return map;
	}
	
	/**
	 * 构建包含十个键值对的Map
	 * 
	 * 创建一个新的HashMap实例，并添加十个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @param key9 第九个键
	 * @param value9 第九个值
	 * @param key10 第十个键
	 * @param value10 第十个值
	 * @param others 其它项,参数数量必须是2的倍数
	 * @return 包含指定键值对的新Map实例
	 */
	public static Map<String, Object> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9, String key10, Object value10, Object... others) {
		return of(String.class, Object.class, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, key8, value8, key9, value9, key10, value10, others);
	}
	
	/**
	 * 构建包含十个键值对的泛型Map
	 * 
	 * 创建一个新的HashMap实例，并添加十个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param clazzT 键的类型Class对象
	 * @param clazzV 值的类型Class对象
	 * @param key1 第一个键
	 * @param value1 第一个值
	 * @param key2 第二个键
	 * @param value2 第二个值
	 * @param key3 第三个键
	 * @param value3 第三个值
	 * @param key4 第四个键
	 * @param value4 第四个值
	 * @param key5 第五个键
	 * @param value5 第五个值
	 * @param key6 第六个键
	 * @param value6 第六个值
	 * @param key7 第七个键
	 * @param value7 第七个值
	 * @param key8 第八个键
	 * @param value8 第八个值
	 * @param key9 第九个键
	 * @param value9 第九个值
	 * @param key10 第十个键
	 * @param value10 第十个值
	 * @param others 其它项,参数数量必须是2的倍数
	 * @return 包含指定键值对的新Map实例
	 */
	@SuppressWarnings("unchecked")
	public static <K, V>Map<K, V> of(Class<K> clazzT, Class<V> clazzV, K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9, K key10, V value10, Object... others) {
		Map<K, V> map = of(clazzT, clazzV, key1, value1, key2, value2, key3, value3, key4, value4, key5, value5, key6, value6, key7, value7, key8, value8, key9, value9);
		map.put(key10, value10);
		if(others != null) {
			for (int i = 0; i < others.length; i=i+2) {
				map.put((K)others[i], (V)others[i+1]);
			}
		}
		return map;
	}

	/**
	 * 构建包含一个键值对的LinkedHashMap
	 * 
	 * 创建一个新的LinkedHashMap实例，并添加一个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param key 第一项的键
	 * @param value 第一项的值
	 * @param others 其它项,参数数量必须是2的倍数
	 * @return LinkedHashMap<String, Object>
	 */
	public static LinkedHashMap<String, Object> ofLinked(String key, Object value, Object... others) {
		return ofLinked(String.class, Object.class, key, value, others);
	}

	/**
	 * 构建包含多个键值对的LinkedHashMap
	 * 
	 * 创建一个新的LinkedHashMap实例，并添加多个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param clazzT 键的类型
	 * @param clazzV 值的类型
	 * @param key1 第一项的键
	 * @param value1 第一项的值
	 * @param others 其它项,参数数量必须是2的倍数
	 * @param <K> K
	 * @param <V> V
	 * @return LinkedHashMap<K, V>
	 */
	public static <K, V>LinkedHashMap<K, V> ofLinked(Class<K> clazzT, Class<V> clazzV, K key1, V value1, Object... others) {
		int size = 1;
		if(others != null)  {
			size += others.length / 2;
		}
		LinkedHashMap<K, V> map = new LinkedHashMap<>(size);
		putAll(map, key1, value1);
		putAll(map, others);
		return map;
	}
	
	/**
	 * 如果条件成立则往map里插值
	 * 
	 * 创建一个新的HashMap实例，并添加一个指定的键值对。
	 * 通过指定键和值的类型，提供类型安全的Map创建方式。
	 * 
	 * @param map map
	 * @param key 键
	 * @param value 值
	 * @param predicate 判断类
	 * @return 条件是否成立
	 */
	public static <K, V>boolean putIf(Map<K, V> map, K key, V value, Predicate<V> predicate) {
		if(predicate.test(value)) {
			map.put(key, value);
			return true;
		}
		return false;
	}
	
	/**
	 * 往map里插入任意多的键值对,采用了强转的方式实现,请自行保证参数类型正确性(others长度为偶数)
	 * @param map map
	 * @param predicate 返回true则插入
	 * @param others 其它项,参数数量必须是2的倍数
	 * @return Map<K, V>
	 */
	@SuppressWarnings("unchecked")
	public static <K, V>Map<K, V> putAllIf(Map<K, V> map, BiPredicate<K, V> predicate, Object... others) {
		if(map == null) {
			map = new HashMap<>(10);
		}
		if(others != null) {
			for (int i = 0; i < others.length; i=i+2) {
				K key = (K)others[i];
				V val = (V) others[i+1];
				if(predicate !=null && predicate.test(key, val))
					map.put(key, val);
			}
		}
		return map;
	}
	
	/**
	 * 往map里插入任意多的键值对,采用了强转的方式实现,请自行保证参数类型正确性(others长度为偶数)
	 * @param map 原始map
	 * @param others 需要插入的键值对
	 * @return Map<K, V>
	 */
	@SuppressWarnings("unchecked")
	public static <K, V>Map<K, V> putAll(Map<K, V> map, Object... others) {
		if(map == null) {
			map = new HashMap<>(10);
		}
		if(others != null) {
			for (int i = 0; i < others.length; i++) {
				map.put((K)others[i], (V)others[++i]);
			}
		}
		return map;
	}
	
	/**
	 * 空指针安全put
	 * 
	 * 如果传入的Map为null，会自动创建一个新的HashMap实例。
	 * 这个方法可以避免NullPointerException，使代码更加健壮。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 目标Map，如果为null会自动创建新的HashMap
	 * @param key 要添加的键
	 * @param value 要添加的值
	 * @return 添加键值对后的Map实例
	 */
	public static <K,V>Map<K, V> put(Map<K, V> map, K key, V value) {
		if(map == null) {
			map = new HashMap<>(1);
		}
		map.put(key, value);
		return map;
	}
	
	/**
	 * 空指针安全的putAll操作
	 * 
	 * 如果目标Map为null，会自动创建一个新的HashMap实例。
	 * 新HashMap的初始容量会根据源Map的大小来优化。
	 * 这个方法可以避免NullPointerException，使代码更加健壮。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 目标Map，如果为null会自动创建新的HashMap
	 * @param extendMap 要合并的源Map
	 * @return 合并后的Map实例
	 */
	public static <K,V>Map<K, V> putAll(Map<K, V> map, Map<K, V> extendMap) {
		if(map == null) {
			map = new HashMap<>(extendMap!=null?extendMap.size():0);
		}
		if(extendMap != null) {
			map.putAll(extendMap);
		}
		return map;
	}
	
	/**
     * 获取map里key对应的值，不存在或null返回defaultValue
     * 
     * 例如：
     * <pre>{@code
     * 		现有值为{a:1,b:2}的map
     *		MapUtils.get(map, a, 2) = 1
     *		MapUtils.get(map, c, 2) = 2
     * }</pre>
     * 
     * @param map 校验的类
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
	@SuppressWarnings("unchecked")
	public static <K,V,T>T get(Map<K, V> map, K key, T defaultValue) {
		try {
			if(map != null && map.containsKey(key) && map.get(key) != null) {
				return (T) map.get(key);
			}
		}catch(Exception ignored) {
		}
		return defaultValue;
	}

	/**
	 * 从Map中获取第一个匹配的键对应的值
	 * 如果Map为空、没有匹配的键、或匹配的值为null，则返回默认值
	 * 此方法用于在多个备选键中寻找第一个有效（非null）的值
	 *
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @param <T> 返回值的类型，与V兼容
	 * @param map 要查询的Map
	 * @param keys 备选键数组，按此顺序查找
	 * @param defaultValue 如果找不到非null值时返回的默认值
	 * @return 第一个匹配的非null值，否则返回默认值
	 */
	public static <K,V,T>T getFirst(Map<K, V> map, K[] keys, T defaultValue) {
	    // 检查Map是否为空或为空Map，如果是，则直接返回null
	    if (map == null || map.isEmpty()) {
	        return null;
	    }
	    try {
	        // 遍历备选键数组
	        for (K key : keys) {
	            // 检查当前键是否在Map中存在
	            if (map.containsKey(key)) {
	                V v = map.get(key);
	                // 如果找到的值非null，则将其转换为T类型并返回
	                if (v != null) {
	                    return (T) v;
	                }
	            }
	        }
	    } catch (Exception ignored) {
	        // 忽略任何异常，即使出现异常也不影响程序继续执行
	    }
	    // 如果所有备选键都不匹配或匹配的值均为null，则返回默认值
	    return defaultValue;
	}
	
	/**
     * 获取map里key对应的值，不存在或null返回null
     * 
     * 例如：
     * <pre>{@code
     * 		现有值为{a:1,b:2}的map
     *		MapUtils.get(map, a) = 1
     *		MapUtils.get(map, c) = null
     * }</pre>
     * 
     * @param map 校验的类
     * @param key 键
     * @return 
     */
	public static <K,V,T>T get(Map<K, V> map, K key) {
		return get(map, key, null);
	}

	/**
	 * 从映射中获取第一个匹配的值
	 *
	 * @param <K> 键的类型
	 * @param <V> 值的类型
	 * @param <T> 返回值的类型
	 * @param map 要查询的映射
	 * @param keys 用于查询的键数组
	 * @return 第一个匹配的值，如果找不到则返回null
	 */
	public static <K,V,T>T getFirst(Map<K, V> map, K[] keys) {
	    return getFirst(map, keys, null);
	}

	/**
	 * 从Map中获取与指定key关联的值，并将其转换为字符串表示形式
	 * 如果指定的key在Map中不存在，或者其关联的值为null，则返回空字符串
	 * 此方法结合了Map的get操作和对象到字符串的转换操作，简化了处理流程
	 *
	 * @param map 要从中获取值的Map。
	 * @param key 要获取其关联值的键。
	 * @return 与指定key关联的值的字符串表示形式，如果值为null或key不存在，则返回空字符串。
	 */
	public static <K,V> String getStr(Map<K, V> map, K key) {
	    return ObjectUtils.toStr(
	            get(map, key));
	}
	
	/**
	 * 获取String
	 * @param map map
	 * @param key key
	 * @param defaultValue defaultValue
	 * @return 与指定key关联的值的字符串表示形式，如果值为null或key不存在，则返回空字符串
	 */
	public static <K,V>String getStr(Map<K, V> map, K key, String defaultValue) {
		return ObjectUtils.toStr(
				get(map, key), defaultValue);
	}

	/**
	 * 获取第一个匹配的键对应的值并转换为字符串
	 * 如果没有找到匹配的键或者值为null，则返回空字符串
	 * 此方法用于简化从Map中获取值并进行类型转换的过程
	 *
	 * @param map   要查询的Map。
	 * @param keys  可能的键数组，方法会尝试使用每个键来获取值，直到找到第一个匹配的键。
	 * @param <K>   键的类型。
	 * @param <V>   值的类型。
	 * @return      第一个匹配的键对应的值的字符串表示，如果没有找到则返回空字符串。
	 */
	public static <K,V> String getFirstStr(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toStr(
	            getFirst(map, keys));
	}

	/**
	 * 获取第一个匹配的键对应的值并转换为字符串
	 * 如果没有找到匹配的键或者值为null，则返回默认值
	 * 此方法扩展了getFirstStr，增加了默认值参数，使得在未找到匹配键时可以返回一个自定义的默认值
	 *
	 * @param map          要查询的Map。
	 * @param keys         可能的键数组，方法会尝试使用每个键来获取值，直到找到第一个匹配的键。
	 * @param defaultValue 默认值，如果没有找到匹配的键或者值为null时返回。
	 * @param <K>          键的类型。
	 * @param <V>          值的类型。
	 * @return             第一个匹配的键对应的值的字符串表示，如果没有找到则返回默认值。
	 */
	public static <K,V> String getFirstStr(Map<K, V> map, K[] keys, String defaultValue) {
	    return ObjectUtils.toStr(
	            getFirst(map, keys), defaultValue);
	}

	/**
	 * 从Map中获取与指定key关联的值，并将其转换为Double类型。
	 * 如果指定的key在Map中不存在，或者其关联的值无法转换为Double类型，则返回null。
	 *
	 * @param map 包含键值对的Map。
	 * @param key 需要获取值的键。
	 * @return 转换为Double类型的值，如果无法获取或转换，则返回null。
	 */
	public static <K,V>Double getDouble(Map<K, V> map, K key) {
	    return ObjectUtils.toDouble(
	            get(map, key));
	}

	/**
	 * 从Map中获取与指定key关联的值，并将其转换为double类型。
	 * 如果指定的key不存在于Map中，或者其关联的值无法转换为double类型，则返回默认值。
	 *
	 * @param map 包含键值对的Map。
	 * @param key 需要获取值的键。
	 * @param defaultValue 如果无法获取或转换值时返回的默认值。
	 * @return 转换为double类型的值，如果无法获取或转换，则返回默认值。
	 */
	public static <K,V>double getDouble(Map<K, V> map, K key, double defaultValue) {
	    return ObjectUtils.toDouble(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取与指定键数组中的第一个匹配键关联的值，并将其转换为Double类型。
	 * 如果键数组中的任何一个键在Map中不存在，或者其关联的值无法转换为Double类型，则返回null。
	 *
	 * @param map 包含键值对的Map。
	 * @param keys 包含可能的键的数组。
	 * @return 转换为Double类型的值，如果无法获取或转换，则返回null。
	 */
	public static <K,V>Double getFirstDouble(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toDouble(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取与指定键数组中的第一个匹配键关联的值，并将其转换为Double类型。
	 * 如果键数组中的任何一个键在Map中不存在，或者其关联的值无法转换为Double类型，则返回默认值。
	 *
	 * @param map 包含键值对的Map。
	 * @param keys 包含可能的键的数组。
	 * @param defaultValue 如果无法获取或转换值时返回的默认值。
	 * @return 转换为Double类型的值，如果无法获取或转换，则返回默认值。
	 */
	public static <K,V>Double getFirstDouble(Map<K, V> map, K[] keys, double defaultValue) {
	    return ObjectUtils.toDouble(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从Map中获取Float值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Float类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @return 键对应的Float值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Float getFloat(Map<K, V> map, K key) {
	    return ObjectUtils.toFloat(
	            get(map, key));
	}

	/**
	 * 从Map中获取float值，如果键不存在或转换失败，则返回默认值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为float类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @param defaultValue 默认值
	 * @return 键对应的float值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>float getFloat(Map<K, V> map, K key, float defaultValue) {
	    return ObjectUtils.toFloat(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取第一个Float值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为Float类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @return 键对应的Float值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Float getFirstFloat(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toFloat(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取第一个float值，如果键不存在或转换失败，则返回默认值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为float类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @param defaultValue 默认值
	 * @return 键对应的float值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>float getFirstFloat(Map<K, V> map, K[] keys, float defaultValue) {
	    return ObjectUtils.toFloat(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从Map中获取Integer值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Integer类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @return 键对应的Integer值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Integer getInt(Map<K, V> map, K key) {
	    return ObjectUtils.toInt(
	            get(map, key));
	}

	/**
	 * 从Map中获取int值，如果键不存在或转换失败，则使用默认值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为int类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @param defaultValue 默认值
	 * @return 键对应的int值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>int getInt(Map<K, V> map, K key, int defaultValue) {
	    return ObjectUtils.toInt(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取第一个Integer值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为Integer类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @return 键对应的Integer值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Integer getFirstInt(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toInt(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取第一个int值，如果键不存在或转换失败，则使用默认值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为int类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @param defaultValue 默认值
	 * @return 键对应的int值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>int getFirstInt(Map<K, V> map, K[] keys, int defaultValue) {
	    return ObjectUtils.toInt(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从Map中获取Long值，如果键不存在或转换失败，则返回null
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Long类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @return 键对应的Long值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Long getLong(Map<K, V> map, K key) {
	    return ObjectUtils.toLong(
	            get(map, key));
	}

	/**
	 * 从Map中获取Long值，如果键不存在或转换失败，则返回默认值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Long类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @param defaultValue 默认值
	 * @return 键对应的Long值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>long getLong(Map<K, V> map, K key, long defaultValue) {
	    return ObjectUtils.toLong(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取第一个Long值，如果键不存在或转换失败，则返回null
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为Long类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @return 键对应的Long值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Long getFirstLong(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toLong(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取第一个long值，如果键不存在或转换失败，则使用默认值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为long类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @param defaultValue 默认值
	 * @return 键对应的long值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>long getFirstLong(Map<K, V> map, K[] keys, long defaultValue) {
	    return ObjectUtils.toLong(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从Map中获取Boolean值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Boolean类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @return 键对应的Boolean值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Boolean getBoolean(Map<K, V> map, K key) {
	    // 使用ObjectUtils将get方法获取的值转换为Boolean
	    return ObjectUtils.toBoolean(
	            get(map, key));
	}

	/**
	 * 从Map中获取Boolean值，如果键不存在或转换失败，则返回默认值
	 * 
	 * 获取指定键对应的值，并尝试将其转换为Boolean类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param key 要获取值的键
	 * @param defaultValue 默认值
	 * @return 键对应的Boolean值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>boolean getBoolean(Map<K, V> map, K key, boolean defaultValue) {
	    // 使用ObjectUtils将get方法获取的值转换为Boolean，如果转换失败则返回默认值
	    return ObjectUtils.toBoolean(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取第一个Boolean值，如果键不存在或转换失败，则返回null
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为Boolean类型。
	 * 如果值不存在或无法转换，返回null。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @return 键对应的Boolean值，如果值不存在或无法转换则返回null
	 */
	public static <K,V>Boolean getFirstBoolean(Map<K, V> map, K[] keys) {
	    // 使用ObjectUtils将getFirst方法获取的值转换为Boolean
	    return ObjectUtils.toBoolean(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取第一个Boolean值，如果键不存在或转换失败，则返回默认值
	 * 
	 * 获取指定键数组中的第一个匹配键对应的值，并尝试将其转换为Boolean类型。
	 * 如果值不存在或无法转换，返回默认值。
	 * 
	 * @param <K> Map键的类型
	 * @param <V> Map值的类型
	 * @param map 源Map
	 * @param keys 键数组
	 * @param defaultValue 默认值
	 * @return 键对应的Boolean值，如果值不存在或无法转换则返回默认值
	 */
	public static <K,V>boolean getFirstBoolean(Map<K, V> map, K[] keys, boolean defaultValue) {
	    // 使用ObjectUtils将getFirst方法获取的值转换为Boolean，如果转换失败则返回默认值
	    return ObjectUtils.toBoolean(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从Map中获取指定键的Date值
	 * 
	 * 支持四种格式
	 *	yyyy-MM-dd HH:mm:ss
	 *	yyyy-MM-dd HH:mm
	 * 	yyyy-MM-dd
	 * 	HH:mm:ss
	 *
	 * @param map 包含键值对的Map
	 * @param key 需要获取的键
	 * @return 对应键的Date值，如果键不存在或转换失败，则返回null
	 */
	public static <K,V>Date getDate(Map<K, V> map, K key) {
	    return ObjectUtils.toDate(
	            get(map, key));
	}

	/**
	 * 从Map中获取指定键的Date值，并指定默认值
	 * 
	 * 支持三种格式
	 * 	yyyy-MM-dd HH:mm:ss
	 * 	yyyy-MM-dd
	 * 	HH:mm:ss
	 *
	 * @param map 包含键值对的Map
	 * @param key 需要获取的键
	 * @param defaultValue 默认的Date值
	 * @return 对应键的Date值，如果键不存在或转换失败，则返回默认值
	 */
	public static <K,V>Date getDate(Map<K, V> map, K key, Date defaultValue) {
	    return ObjectUtils.toDate(
	            get(map, key), defaultValue);
	}

	/**
	 * 从Map中获取多个键中的第一个Date值
	 * 
	 * 支持四种格式
	 *	yyyy-MM-dd HH:mm:ss
	 *	yyyy-MM-dd HH:mm
	 * 	yyyy-MM-dd
	 * 	HH:mm:ss
	 *
	 * @param map 包含键值对的Map
	 * @param keys 需要尝试获取的键数组
	 * @return 第一个找到的Date值，如果键不存在或转换失败，则返回null
	 */
	public static <K,V>Date getFirstDate(Map<K, V> map, K[] keys) {
	    return ObjectUtils.toDate(
	            getFirst(map, keys));
	}

	/**
	 * 从Map中获取多个键中的第一个Date值，并指定默认值
	 * 
	 * 支持三种格式
	 * 	yyyy-MM-dd HH:mm:ss
	 * 	yyyy-MM-dd
	 * 	HH:mm:ss
	 *
	 * @param map 包含键值对的Map
	 * @param keys 需要尝试获取的键数组
	 * @param defaultValue 默认的Date值
	 * @return 第一个找到的Date值，如果键不存在或转换失败，则返回默认值
	 */
	public static <K,V>Date getFirstDate(Map<K, V> map, K[] keys, Date defaultValue) {
	    return ObjectUtils.toDate(
	            getFirst(map, keys), defaultValue);
	}
	
	/**
	 * 从给定的Map中移除指定的键
	 *
	 * @param <K> Map中键的类型
	 * @param <V> Map中值的类型
	 * @param map 要从中移除键的Map
	 * @param key 要移除的键
	 * @return 移除指定键后的Map如果输入的Map为null，则返回null
	 */
	public <K,V> Map<K,V> remove(Map<K, V> map, K key) {
	    // 检查输入的Map是否为null，如果为null则返回null
	    if(map == null) {
	        return null;
	    }
	    // 从Map中移除指定的键
	    map.remove(key);
	    // 返回移除指定键后的Map
	    return map;
	}
	
	/**
	 * 转换map中的key
	 * 
	 * @param src 需要进行key转换的源map
	 * @param convertMap 源key和转换的目标key的对应map
	 * @return 返回转换key后的map
	 */
	public static <K,V>Map<K,V> convertKeys(Map<K,V> src, Map<K,K> convertMap) {
	    // 遍历转换map的key集合
	    Iterator<K> itor = convertMap.keySet().iterator();
	    while(itor.hasNext()) {
	        // 获取当前遍历的源key
	        K key1 = itor.next();
	        // 获取源key对应的转换后的key
	        K key2 = convertMap.get(key1);
	        // 调用convertKey方法进行key的转换
	        convertKey(src, key1, key2);
	    }
	    // 返回转换key后的源map
	    return src;
	}
	
	/**
	 * 转换map中的key
	 * <p>
	 * 	流程:判断源map中是否有键key1,保存key2及key1对应的值，删除键key1，返回源map
	 * </p>
	 * 
	 * @param src src
	 * @param key1 key1
	 * @param key2 key2
	 * @return 转换后的map
	 */
	public static <K,V>Map<K,V> convertKey(Map<K,V> src, K key1, K key2) {
		if(isEmpty(src)) {
			return src;
		}
		if(src.containsKey(key1)) {
			src.put(key2, src.get(key1));
			src.remove(key2);
		}
		return src;
	}
	
	/**
	 * 格式化map为字符串
	 * @param map map
	 * @param separatorItem separatorItem
	 * @param separatorKeyValue separatorKeyValue
	 * @return map转字符串，形如key1=value1,key2=value2,key3=value3
	 */
	public static <K,V>String toString(Map<K,V> map, String separatorItem, String separatorKeyValue) {
		if(isEmpty(map)) {
			return "";
		}
		StringBuilder sb = null;
        for (K k : map.keySet()) {
            if (sb == null) {
                sb = new StringBuilder();
            } else if (separatorItem != null) {
                sb.append(separatorItem);
            }
            V value = map.get(k);
            sb.append(k);
            if (separatorKeyValue != null) {
                sb.append(separatorKeyValue);
            }
            sb.append(value);
        }
        if (sb != null) {
            return sb.toString();	//理论上不用考虑sb为null的情况，因为map不为空
        }
		return "";
    }
	
	/**
     * 对linkedHashedMap进行排序
     *
     * <a href="https://blog.csdn.net/qq997404392/article/details/73333215">参考</a>
     *
     * @param src 源map
     * @param comparator 排序器
     * @return 排序后的map
     */
	public static <K,V>Map<K,V> sort(LinkedHashMap<K,V> src, Comparator<Entry<K, V>> comparator) {
		if(isEmpty(src)) {
			return src;
		}
		//先转成ArrayList集合
		List<Entry<K, V>> list = 
		        new ArrayList<>(src.entrySet());
		Collections.sort(list, comparator);
		//清空源map，把排序后的List放入
		src.clear();
		for (Entry<K, V> entry : list) {
			src.put(entry.getKey(), entry.getValue());
		}
		return src;
	}
	
	/**
     * 将List<Map>中的键值重新组合成新的Map
     * 
     * 可以理解为纵表转横表。
     * 例如：现有值为[{a:key1,b:2}{a:key2,b:3}]的list，
     * MapUtils.of(list, "a", "b") = {key1:2, key2:3}
     * 
     * 
     * @param list 源数据列表
     * @param keyTitle 作为新map的key的键名
     * @param keyValue 作为新map的value的键名
     * @return 重组后的Map，如果list为null则返回空Map
     */
	public static Map<String, Object> ofMap(List<Map<String, Object>> list, String keyTitle, String keyValue) {
		if(list == null) {
			return new HashMap<>();
		}
		Map<String, Object> map = new HashMap<>();
		for (Map<String, Object> item : list) {
			map.put(item.get(keyTitle).toString(), item.get(keyValue));
		}
		return map;
	}
	
	/**
	 * 根据分隔符拆分字符串得到Map
	 * 
	 * 注意分隔符都是正则表达式（注意转义问题）。
	 * key-value分割符无法拆分的项会被略过，不会报错。
	 * 
	 * @param src 源字符串
	 * @param separatorItem 分割Map每一项的分隔符
	 * @param separatorKeyValue 分割每一项key-value的分隔符
	 * @return LinkedHashMap实例，保留原有顺序，如果src为空则返回空Map
	 */
	public static Map<String, Object> ofMap(String src, String separatorItem, String separatorKeyValue) {
		if(StringUtils.isEmpty(src)) {
			return new HashMap<>();
		}
		Map<String, Object> map = new LinkedHashMap<>();
		String[] items = src.split(separatorItem);
		for (String item : items) {
			String[] keyValue = item.split(separatorKeyValue);
			if(keyValue.length == 2) {
				map.put(keyValue[0], keyValue[1]);
			}
		}
		return map;
	}
	
}