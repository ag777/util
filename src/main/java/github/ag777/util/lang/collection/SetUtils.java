package github.ag777.util.lang.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * 有关 <code>Set</code> 集合工具类。
 * 
 * @author ag777
 * @version create on 2018年03月23日,last modify at 2018年06月14日
 */
public class SetUtils {

	private SetUtils() {}
	
	
	/**
	 * 根据分隔符拆分Set获取字符串
	 * @param set set
	 * @param separator separator
	 * @return 拼接好的字符串
	 */
	public static <T>String toString(Set<T> set, String separator) {
		if(CollectionAndMapUtils.isEmpty(set)) {
			return "";
		}
		StringBuilder sb = null;
        for (T t : set) {
            if (sb == null) {
                sb = new StringBuilder();
            } else if (separator != null) {
                sb.append(separator);
            }
            if (t != null) {
                sb.append(t.toString());
            } else {
                sb.append("null");
            }
        }
        return sb.toString();
 	}
	
	/**
	 * 求两个集合的交集
	 * @param set1 set1
	 * @param set2 set2
	 * @return 交集
	 */
	public static <T>Set<T> intersection(Set<T> set1, Set<T> set2) {
		Set<T> result = new HashSet<>(set1.size()+set2.size());
		result.addAll(set1);
        result.retainAll(set2);
        return result;
	}
	
	/**
	 * 求两个集合的并集
	 * @param set1 set1
	 * @param set2 set2
	 * @return 并集
	 */
	public static <T>Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> result = new HashSet<>(set1.size()+set2.size());
		result.addAll(set1);
		result.addAll(set1);
        result.addAll(set2);
        return result;
	}
	
	/**
	 * 求两个集合的补集(差集)
	 * @param set1 set1
	 * @param set2 set2
	 * @return 补集
	 */
	public static <T>Set<T> complement(Set<T> set1, Set<T> set2) {
		Set<T> result = new HashSet<>(set1.size()+set2.size());
		result.addAll(set1);
		result.addAll(set1);
        result.removeAll(set2);
        return result;
	}
	
}