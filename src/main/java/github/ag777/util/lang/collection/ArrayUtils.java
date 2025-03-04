package github.ag777.util.lang.collection;

import java.util.Arrays;
import java.util.Optional;

/**
 * 有关数组操作的工具类。
 * 
 * @author ag777
 * @version create on 2024年02月20日
 */
public class ArrayUtils {

    private ArrayUtils() {}

    /**
     * 复制数组
     * <p>
     * 详见Arrays.copyOf(T[] original, int newLength)方法
     * </p>
     * 
     * @param original 原始数组
     * @param newLength 新数组的长度
     * @return 复制后的新数组
     */
    public static <T>T[] copyArray(T[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }
    
    /**
     * 判断数组是否为空
     * 
     * @param array 待检查的数组
     * @return true:数组为null或空, false:数组不为空
     */
    public static <T>boolean isEmpty(T[] array) {
        return CollectionAndMapUtils.isEmpty(array);
    }

    /**
     * 获取一个元素在数组中的位置
     * <p>
     *  用equals实现比较<br>
     *  通过调用isPresent()方法直接获取是否在数组中,不需要判断值是否大于-1
     * </p>
     * @param array 要搜索的数组
     * @param item 要查找的元素
     * @return 如果找到元素则返回包含索引位置的Optional,否则返回空Optional
     */
    public static <T>Optional<Integer> inArray(T[] array, Object item) {
        if(array == null || item==null) {
            return Optional.empty();
        }
        for(int i=0;i<array.length;i++) {
            if(item.equals(array[i])) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 获取字符串在字符串数组里的位置,大小写无视
     * @param array 要搜索的字符串数组
     * @param item 要查找的字符串
     * @return 如果找到字符串则返回包含索引位置的Optional,否则返回空Optional
     */
    public static Optional<Integer> inArrayIgnoreCase(String[] array, String item) {
        if(array == null || item==null) {
            return Optional.empty();
        }
        for(int i=0;i<array.length;i++) {
            if(item.equalsIgnoreCase(array[i])) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 获取数组类型
     * @param array 要获取类型的数组
     * @return 数组的组件类型,如果数组为null则返回null
     */
    @SuppressWarnings("unchecked")
    public static <T>Class<T> getClass(T[] array) {
        if(array == null) {
            return null;
        }
        return (Class<T>) array.getClass().getComponentType();
    }

    /**
     * 从数组中获取指定索引的元素。
     * 如果数组为空或索引超出范围，则返回null。
     *
     * @param array 输入的泛型数组。
     * @param index 要获取元素的索引。
     * @param <T> 泛型参数，表示数组的元素类型。
     * @return 如果数组有效且索引存在，则返回对应元素；否则返回null。
     */
    public static <T>T get(T[] array, int index) {
        if(array == null || array.length<=index) {
            return null;
        }
        return array[index];
    }

    /**
     * 从数组中获取指定索引的元素，如果元素为null，则返回默认值。
     * 这个方法为调用者提供了处理null值的灵活性，避免了直接的NullPointerException。
     *
     * @param array 输入的泛型数组。
     * @param index 要获取元素的索引。
     * @param defaultValue 如果指定索引的元素为null，将返回此默认值。
     * @param <T> 泛型参数，表示数组的元素类型。
     * @return 如果数组有效且索引存在且元素不为null，则返回对应元素；否则返回默认值。
     */
    public static <T>T get(T[] array, int index, T defaultValue) {
        T item = get(array, index);
        // 检查获取的元素是否为null，为null则返回默认值
        if (item == null) {
            return defaultValue;
        }
        return item;
    }
} 