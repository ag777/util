package github.ag777.util.lang;

import github.ag777.util.gson.GsonUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;

/**
 * @author ag777 <837915770@vip.qq.com>
 * @version 2025/4/2 下午11:15
 */
public class ObjectUtils {
    /**
     * 将对象转换为字符串表示。
     *
     * @param obj 要转换的对象
     * @return 对象的字符串表示
     */
    public static String toStr(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Date) {
            return DateUtils.toString((Date) obj, DateUtils.DEFAULT_TEMPLATE_TIME);
        } else if (obj instanceof java.time.LocalDateTime) {
            return DateUtils.toString((java.time.LocalDateTime) obj, DateUtils.FORMATTER_TIME_JAVA);
        } else if (obj instanceof java.time.LocalDate) {
            return DateUtils.toString((java.time.LocalDate) obj, DateUtils.FORMATTER_DATE_JAVA);
        } else if (obj instanceof java.time.LocalTime) {
            return DateUtils.toString((java.time.LocalTime) obj, DateUtils.FORMATTER_HH_MM_SS_JAVA);
        }
        return String.valueOf(obj);
    }

    public static String toStr(Object obj, String defaultValue) {
        String result = toStr(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Double
     */
    public static Double toDouble(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Double) {
            return (Double) obj;
        } else if(obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Long) {
            return ((Long)obj).doubleValue();
        } else if (obj instanceof Float) {
            return ((Float)obj).doubleValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).doubleValue();
        }

        return StringUtils.toDouble(obj.toString());
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return double
     */
    public static double toDouble(Object obj, double defaultValue) {
        Double result = toDouble(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Float
     */
    public static Float toFloat(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Float) {
            return (Float) obj;
        } else if(obj instanceof Integer) {
            return ((Integer) obj).floatValue();
        } else if (obj instanceof Long) {
            return ((Long)obj).floatValue();
        } else if (obj instanceof Double) {
            return ((Double)obj).floatValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).floatValue();
        }

        return StringUtils.toFloat(obj.toString());
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return float
     */
    public static float toFloat(Object obj, float defaultValue) {
        Float result = toFloat(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Integer
     */
    public static Integer toInt(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Float) {
            return ((Float)obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double)obj).intValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).intValue();
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? 1 : 0;
        }

        return StringUtils.toInt(obj.toString());
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return int
     */
    public static int toInt(Object obj, int defaultValue) {
        Integer result = toInt(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Long
     */
    public static Long toLong(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Long) {
            return (Long) obj;
        } else if(obj instanceof Integer) {
            return (long) obj;
        } else if (obj instanceof Float) {
            return ((Float)obj).longValue();
        } else if (obj instanceof Double) {
            return ((Double)obj).longValue();
        } else if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        } else if(obj instanceof Date) {
            return ((Date)obj).getTime();
        }

        return StringUtils.toLong(obj.toString());
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return long
     */
    public static long toLong(Object obj, long defaultValue) {
        Long result = toLong(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Boolean
     */
    public static Boolean toBoolean(Object obj) {
        if(obj != null) {
            if(obj instanceof Boolean) {
                return (Boolean) obj;
            } else {
                return StringUtils.toBoolean(obj.toString());
            }
        }
        return null;
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return boolean
     */
    public static boolean toBoolean(Object obj, boolean defaultValue) {
        Boolean result = toBoolean(obj);
        return getOrDefault(result, defaultValue);
    }

    /**
     *
     * @param obj obj
     * @return Date
     */
    public static Date toDate(Object obj) {
        if(obj != null) {
            if(obj instanceof Date) {
                return (Date) obj;
            } else if (obj instanceof java.time.LocalDateTime) {
                return Date.from(((java.time.LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant());
            } else if (obj instanceof java.time.LocalDate) {
                return Date.from(((java.time.LocalDate) obj).atStartOfDay(ZoneId.systemDefault()).toInstant());
            } else if (obj instanceof Long){
                // 时间戳
                return new Date((long) obj);
            } else if(obj instanceof Integer) {
                // 去掉毫秒级的时间戳
                return new Date((int) obj * 1000);
            } else {
                return StringUtils.toDate(obj.toString());
            }
        }
        return null;
    }

    /**
     *
     * @param obj obj
     * @param defaultValue 默认值
     * @return Date
     */
    public static Date toDate(Object obj, Date defaultValue) {
        Date result = toDate(obj);
        return getOrDefault(result, defaultValue);
    }

    public static Map<String, Object> toMap(Object obj) {
        if(obj == null) {
            return null;
        }
        return GsonUtils.get().toMap(GsonUtils.get().toJson(obj));
    }

    public static <T> List<Map<String, Object>> toListMap(List<T> list) {
        if(list == null) {
            return null;
        }
        if(list.isEmpty()) {
            return Collections.emptyList();
        }
        GsonUtils u = GsonUtils.get();
        return u.toListMap(u.toJson(list));
    }

    //其它
    /**
     * 如果obj为null则返回默认值
     * @param obj obj
     * @param defaultValue defaultValue
     * @return obj不为null返回obj,否则返回defaultValue
     */
    public static <T>T getOrDefault(T obj, T defaultValue) {
        return obj != null?obj:defaultValue;
    }

    /**
     * 按参数优先级，判断直至参数不为null，则返回
     * @param obj 第一个参数
     * @param obj2 第二个参数
     * @param obj3 第三个参数
     * @param objs 其它参数
     * @return 不为null的参数或最后一个参数
     * @param <T> T
     */
    @SafeVarargs
    public static <T>T getOrDefault(T obj, T obj2, T obj3, T... objs) {
        if (obj != null) {
            return obj;
        }
        if (obj2 != null) {
            return obj2;
        }
        if (objs != null) {
            if (obj3 != null) {
                return obj3;
            }
            for (int i = 0; i < objs.length-1; i++) {
                T o = objs[i];
                if (o != null) {
                    return o;
                }
            }
            return objs[objs.length-1];
        }
        return obj3;
    }

    /**
     * 判断对象是否为空
     * <p>
     * 	支持字符串，数组，列表，map;<br>
     * 此外都返回false;
     * 	ObjectUtils.isEmpty(new int[]{}) = false<br>
     *
     */
    @SuppressWarnings({ "rawtypes" })
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {	//字符串
            return StringUtils.isEmpty((String) obj);
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {	//数组
            return true;
        }
        if (obj instanceof Collection) {	//列表
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {	//map
            return ((Map) obj).isEmpty();
        }
        return false;
    }


    /**
     * 获取对象长度
     * <p>
     * 	支持字符串(一个汉字占两个字节)，数组，列表，map;<br>
     * 此外都返回-1<br>
     * 	ObjectUtils.isEmpty(new int[]{1,2,3}) = 3<br>
     *
     */
    @SuppressWarnings("rawtypes")
    public static int getLength(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof String) {	//字符串
            return StringUtils.getLength((String) obj);
        }
        if (obj.getClass().isArray()) {	//数组
            return Array.getLength(obj);
        }
        if (obj instanceof Collection) {	//列表
            return ((Collection) obj).size();
        }
        if (obj instanceof Map) {	//map
            return((Map) obj).size();
        }
        return -1;
    }


    //--判断
    /**
     * 判断对象是否为数值类型
     * @param obj obj
     * @return 是否为数值类型
     * @throws Exception Exception
     */
    public static boolean isNumber(Object obj) throws Exception {
        if(obj == null) {
            throw new RuntimeException("对象为空,不能判断是否为数组");
        }
        return Number.class.isAssignableFrom(obj.getClass());
    }

    /**
     * @param obj obj
     * @return 是否为数组
     */
    public static boolean isArray(Object obj) {
        if(obj == null) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * @param obj obj
     * @return 是否为collection
     */
    public static boolean isCollection(Object obj) {
        if(obj == null) {
            return false;
        }
        return obj instanceof Collection;
    }

    /**
     * 判断Boolean变量是否为true，防止控指针异常
     * @param bool bool
     * @return 是否为true
     */
    public static boolean isBooleanTrue(Boolean bool) {
        return bool != null && bool;
    }

    /**
     * 空指针安全equals
     * <p>
     * 	参数中其中一个为null则返回false
     * </p>
     *
     * @param a a
     * @param b b
     * @return 是否相同
     */
    public static boolean equals(Object a, Object b) {
        if(a==null || b==null) {
            return false;
        }
        return a.equals(b);
    }
}
