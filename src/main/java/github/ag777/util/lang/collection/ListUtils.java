package github.ag777.util.lang.collection;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 有关 <code>List</code> 列表工具类。
 * 
 * @author ag777
 * @version create on 2017年09月22日,last modify at 2024年02月20日
 */
public class ListUtils {

	private ListUtils(){}

	
	/**
	 * 判断集合是否为空
	 * 
	 * @param collection 待检查的集合
	 * @return true:集合为null或空, false:集合不为空
	 */
	public static <E>boolean isEmpty(Collection<E> collection) {
		return CollectionAndMapUtils.isEmpty(collection);
	}
	
	/**
	 * 构建list
	 * <p>
	 * 	含任意多项,将传入的可变参数构造成一个新的ArrayList
	 * </p>
	 * 
	 * @param items 可变参数列表
	 * @return 包含传入元素的新ArrayList
	 */
	@SafeVarargs
	public static <T>List<T> of(T... items) {
		if (ArrayUtils.isEmpty(items)) {
			return new ArrayList<>(0);
		}
		List<T> list = new ArrayList<>(items.length);
		list.addAll(Arrays.asList(items));
		return list;
	}


	
	/**
	 * 统计列表中每个元素及其出现次数
	 * <p>
	 * 将列表中的元素作为键，其出现次数作为值，构建一个映射关系。
	 * </p>
	 * <p>示例：</p>
	 * <pre>{@code
	 * toCountMap(of("a","b","a"))=>{"a":2,"b":1}
	 * }</pre>
	 * 
	 * @param <T> 列表元素的类型
	 * @param list 待统计的列表
	 * @return 包含元素及其出现次数的Map，key为元素，value为出现次数
	 */
	public <T>Map<T, Long> toCountMap(List<T> list) {
		return list.stream().collect(Collectors.groupingBy(p -> p,Collectors.counting()));
	}

	
    /**
     * 删除列表中某项元素，避免{@code List<Integer>}的下标陷阱
     * <p>
     * 此方法提供了一种安全的方式来删除列表中的元素，特别是对于{@code List<Integer>}类型的列表。
     * 它避免了{@code remove()}方法的重载歧义问题，确保正确删除指定的元素而不是指定索引位置的元素。
     * </p>
     * <p>示例：</p>
     * <pre>{@code
     * List<Integer> list = new ArrayList<>();
     * list.add(1);
     * list.add(2);
     * list.add(3);
     * // 这时我们需要删除值为2的元素,如果直接调用
     * list.remove(2);
     * // 结果是[1,2]而不是预期的[1,3]，因为remove(2)被解释为删除索引2的元素
     * // 使用此方法可以正确删除值为2的元素
     * ListUtils.remove(list, 2); // 结果为[1,3]
     * }</pre>
     * 
     * @param <T> 列表元素的类型
     * @param list 待操作的列表
     * @param item 要删除的元素
     * @return 删除元素后的列表，如果输入列表为null则返回null
     */
    public static <T>List<T> remove(List<T> list, Object item) {
    	if(list == null) {
    		return null;
    	}
    	list.remove(item);
    	return list;
    }
    
    
    /**
     * 列表分段(将一个列表，每limit长度分一个列表并将这些新列表整合到一个列表里)
     * <p>
     * 该方法用于解决一次性批量插入数据到数据库时数据量太大导致插入失败的问题，通过将列表数据分次插入来解决。
     * </p>
     * <p>示例：</p>
     * <pre>{@code
     * 现有值为[1,2,3]的列表list,执行
     * ListUtils.splitList(list, 2);
     * 将会得到值为[[1,2],[3]]的嵌套列表
     * }</pre>
     * 
     * <p><strong>注意：</strong>拆分方法使用了List中的subList方法，该方法会引发一个比较隐蔽的问题
     * (可能会抛出java.util.ConcurrentModificationException异常)。
     * 如果需要对拆分后的列表进行操作，建议使用 {@link #splitList2(List, int)} 方法，
     * 或者采用其它方式绕过对subList对象的操作。</p>
     * 
     * @param <T> 列表元素的类型
     * @param list 需要分段的源列表
     * @param limit 每段列表的最大长度
     * @return 分段后的列表的列表，如果输入为null则返回null
     * @throws RuntimeException 当limit小于等于0时抛出此异常
     */
	public static <T>List<List<T>> splitList(List<T> list, int limit) {
		if(list == null) {
			return null;
		}
		if(limit <= 0) {
			throw new RuntimeException("参数limit必须大于0");
		}
		int size = list.size()/limit+1;
		List<List<T>> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			int min = limit*i;
			int max = limit*(i+1);
			max = Math.min(max, list.size());
			if(max > min) {
				List<T> item = list.subList(min, max);
				result.add(item);
			}
			
		}
		return result;
	}
	
	/**
	 * 列表分段(将一个列表，每limit长度分一个列表并将这些新列表整合到一个列表里)
	 * <p>
	 * 此方法是 {@link #splitList(List, int)} 的改进版本，通过创建新的ArrayList来存储子列表，
	 * 避免了对原列表的依赖关系，从而规避了ConcurrentModificationException的风险。
	 * 虽然需要创建更多的列表对象，但在并发操作时更安全。
	 * </p>
	 * 
	 * @param <T> 列表元素的类型
	 * @param list 需要分段的源列表
	 * @param limit 每段列表的最大长度
	 * @return 分段后的列表的列表，如果输入为null则返回null，如果输入为空列表则返回空的嵌套列表
	 * @throws RuntimeException 当limit小于等于0时抛出此异常
	 */
	public static <T>List<List<T>> splitList2(List<T> list, int limit) {
		if(list == null) {
			return null;
		}
		if(limit <= 0) {
			throw new RuntimeException("参数limit必须大于0");
		} 
		
		int size = list.size();
		List<List<T>> resultList = new ArrayList<>(size);
		if(size == 0) {
			return resultList;
		}
		int remainder = size % limit;  //(先计算出余数)
	    int number = size / limit;  //然后是商
	    if(remainder>0) {
	    	number+=1;
	    }
		for (int i = 0; i<number;i=i+1) {
			List<T> l = new ArrayList<>(limit);
			int z =i*limit;
			for (int j = 0; j < limit; j++) {
				int k = z+j;
				if(k>=size) {
					break;
				}
				l.add(list.get(k));
			}
			resultList.add(l);
		}
		return resultList;
	}
	
	//--复制
	
	/**
	 * 深度复制列表（通过序列化和反序列化实现）
	 * <p>
	 * 此方法通过序列化和反序列化机制创建列表的深度副本，确保副本中的所有元素都是原列表元素的完全独立复制。
	 * 使用此方法要求列表中的所有元素都实现了 {@link Serializable} 接口。
	 * </p>
	 * 
	 * @param <T> 列表元素的类型，必须实现 Serializable 接口
	 * @param src 源列表
	 * @return 源列表的深度副本
	 * @throws IOException 如果序列化或反序列化过程中发生I/O错误
	 * @throws ClassNotFoundException 如果反序列化时找不到类定义
	 */
	public static <T extends Serializable> List<T> deepCopy(List<T> src) throws Exception {
		try {
		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
		    ObjectOutputStream out = new ObjectOutputStream(byteOut);  
		    out.writeObject(src);  
		  
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
		    ObjectInputStream in = new ObjectInputStream(byteIn);  
		    @SuppressWarnings("unchecked")  
		    List<T> dest = (List<T>) in.readObject();  
		    return dest;
		} catch(IOException|ClassNotFoundException ex) {
			throw ex;
		}
	}

	/**
	 * 判断字符串在列表中的位置（忽略大小写）
	 * <p>
	 * 此方法会遍历列表，查找与给定字符串相等（忽略大小写）的元素，并返回其索引位置。
	 * 如果找不到匹配的元素或输入参数无效，则返回一个空的 Optional。
	 * </p>
	 * 
	 * @param list 要搜索的字符串列表
	 * @param item 要查找的字符串
	 * @return 包含匹配元素索引的Optional，如果未找到则返回空Optional
	 */
	public static Optional<Integer> inListIgnoreCase(List<String> list, String item) {
		if(isEmpty(list) || item == null) {
			return Optional.empty();
		}
		for(int i=0; i<list.size(); i++) {
			if(item.equalsIgnoreCase(list.get(i))) {
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * 从列表中获取指定索引的元素。
	 *
	 * 此方法提供了一种安全的方式来访问列表中的元素，通过检查索引是否超出列表大小，
	 * 避免了IndexOutOfBoundsException的抛出。
	 *
	 * @param list 目标列表，从中获取元素。
	 * @param index 要获取元素的索引。
	 * @return 如果索引有效，则返回列表中对应索引的元素；如果索引超出列表范围，则返回null。
	 * @param <T> 泛型参数，表示列表和返回值的类型。
	 */
	public static <T>T get(List<T> list, int index) {
		// 检查索引是否超出列表范围，如果超出，则直接返回null
		if (index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

	/**
	 * 从列表中获取指定索引的元素，如果索引超出范围或元素为null，则返回默认值。
	 * 这个方法提供了对列表访问时的容错能力，确保了即使在访问不存在或为空的元素时，也能返回一个预设的默认值。
	 *
	 * @param list 要访问的列表。
	 * @param index 要获取元素的索引。
	 * @param defaultValue 如果索引超出范围或元素为null时返回的默认值。
	 * @param <T> 泛型参数，表示列表和返回值的类型。
	 * @return 列表中指定索引的元素，如果索引超出范围或元素为null，则返回默认值。
	 */
	public static <T>T get(List<T> list, int index, T defaultValue) {
		T item = get(list, index);
		// 检查获取的元素是否为null，为null则返回默认值
		if (item == null) {
			return defaultValue;
		}
		return item;
	}
}