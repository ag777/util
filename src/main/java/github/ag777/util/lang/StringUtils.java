package github.ag777.util.lang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * 
 * @author ag777
 * @version last modify at 2020年09月23日
 */
public class StringUtils {

	private static final Pattern PATTERN_EMPTY = Pattern.compile("^[\\u00A0\\s　]*$");	//空值验证(\\u00A0为ASCII值为160的空格)

	/**
	 * 生成随机的uuid,去除横线
	 * 
	 * @return 32位的uuid字符串
	 */
	public static String uuid(){
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 获得字符串长度(utf-8编码)
	 * @see #getLength(String, Charset)
	 * 
	 * @param src 源字符串
	 * @return 字符串按UTF-8编码后的字节长度,如果字符串为空返回0
	 */
	public static int getLength(String src) {
//		src = src.replaceAll("[^\\x00-\\xff]", "**");
//		int length = src.length();
		return getLength(src, StandardCharsets.UTF_8);
	}
	
	/**
	 * 获得字符串长度<br>
	 * 实现原理:String.getBytes(charset).length
	 * 
	 * @param src 源字符串
	 * @param charset 字符编码
	 * @return 字符串按指定编码后的字节长度,如果字符串为空返回0
	 */
	public static int getLength(String src, Charset charset) {
		if(isEmpty(src)) {
			return 0;
		}
		return src.getBytes(charset).length;
	}
	
	/**
	 * 判断字符串是否为null或者长度为0
	 * 
	 * @param src 待检查的字符串
	 * @return true:字符串为null或长度为0; false:字符串不为null且长度大于0
	 */
	public static boolean isEmpty(String src) {
        return src == null || src.isEmpty();
    }
	
	/**
	 * 判断字符串是否为null获取为空字符串(最多只含制表符 \t ('\u0009'),换行符 \n ('\u000A'),回车符 \r ('\u000D')，换页符 \f ('\u000C')以及半角/全角空格)
	 * 
	 * @param src 待检查的字符串
	 * @return true:字符串为null或只包含空白字符; false:字符串包含非空白字符
	 */
	public static boolean isBlank(String src) {
        return src == null || PATTERN_EMPTY.matcher(src).matches();
    }
	
	/**
	 * 如果字符串为null则返回空字符串,否则返回对象的字符串表示
	 * 
	 * @param src 源对象
	 * @return 如果对象为null返回空字符串"",否则返回对象的toString()结果
	 */
	public static String emptyIfNull(Object src) {
		if (src == null) {
			return "";
		}
		return src.toString();
	}

	/**
	 * 通过StringBuilder连接字符串
	 *
	 * @param obj 	第一个值
	 * @param objs 	后续的值
	 * @return 拼接好的文字
	 */
	public static String concat(Object obj, Object... objs) {
		StringBuilder sb = new StringBuilder();
		sb.append(obj);
		if(objs != null) {
			for (Object item : objs) {
				if(item !=null) {
					sb.append(item);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * {@code
	 *  summary("aaa",5,"..."); => "aa..."
	 *  summary("aaa",2,"..."); => ".."
	 *  summary("aaa",7,"..."); => "aaa..."
	 * }
	 * @param src 原文字
	 * @param limit 限制长度
	 * @param ellipsis 省略号
	 * @return 文字摘要
	 */
	public static String summary(String src, int limit, String ellipsis) {
		if(src == null || limit<=0) {
			return "";
		}
		if(src.length()<=limit) {
			return src;
		}
		ellipsis = StringUtils.emptyIfNull(ellipsis);
		int end = limit-ellipsis.length();
		if(end > 0) {	//加上省略号超长
			return src.substring(0, end)+ellipsis;
		} else if(end == 0) {	//限制数刚好等于省略号长度
			return ellipsis;
		} else {	//限制数小于省略号长度
			return ellipsis.substring(0, limit);
		}
	}

	/**
	 * 利用StringBuilder倒置字符串
	 * 例如: "abc" -> "cba"
	 * 如果输入为null或空字符串,则直接返回输入值
	 * 
	 * @param src 需要倒置的源字符串
	 * @return 倒置后的字符串
	 */
	public static String reverse(String src) {
		if(isEmpty(src)) {
			return src;
		}
		return new StringBuilder(src).reverse().toString();
	}
	
	/**
     * 下划线转驼峰法
     * {@code
     *  underline2Camel("hello_world", true) => "helloWorld"
     *  underline2Camel("hello_world", false) => "HelloWorld" 
     *  underline2Camel("_user_name", true) => "userName"
     *  underline2Camel("user__name", true) => "userName"
     *  underline2Camel("USER___NAME", true) => "userName"
     *  underline2Camel("", true) => ""
     * }
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰（如果为false则首字母大写）
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (isEmpty(line)) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(line.length());
        boolean upperCase = !smallCamel;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '_') {
                upperCase = true;
            } else {
                if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        
        return sb.toString();
    }
	
	
	/**
	 * 首字母大写
	 * 例如: "abc" -> "Abc"
	 * 如果输入为null或空字符串,则直接返回输入值
	 * 
	 * @param src 源字符串
	 * @return 首字母大写后的字符串
	 */
	public static String upperCaseFirst(String src) {  
		if(isEmpty(src)) {
			return src;
		}
	    char[] ch = src.toCharArray();  
	    if (ch[0] >= 'a' && ch[0] <= 'z') {  
	        ch[0] = (char) (ch[0] - 32);  
	    }  
	    return new String(ch);  
	}
	/**
	 * 驼峰转下划线
	 * 例如: "helloWorld" -> "hello_world"
	 * 如果输入为null或空字符串,则返回空字符串
	 * 
	 * @param str 源字符串
	 * @return 转换后的下划线字符串
	 */
	public static String camel2Underline(String str) {
		if (isEmpty(str)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					sb.append('_');
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 格式化数字,保留指定位数的小数
	 * 例如: formatNum(3.1415926, 2) -> "3.14"
	 * 
	 * @param num 需要格式化的数字
	 * @param decimalPlaces 保留小数位数
	 * @return 格式化后的字符串
	 */
	public static String formatNum(double num, int decimalPlaces) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		return nf.format(decimalPlaces);
	}
	
	/**
	 * 字符串转Double
	 * 例如: "3.14" -> 3.14
	 * 转换失败时返回null
	 * 
	 * @param src 源字符串
	 * @return 转换后的Double对象,转换失败返回null
	 */
	public static Double toDouble(String src) {
		try {
			return Double.parseDouble(src);
		} catch(Exception ex) {
			//转换失败
		}
		return null;
	}
	
	/**
	 * 字符串转Float
	 * 例如: "3.14" -> 3.14f
	 * 转换失败时返回null
	 * 
	 * @param src 源字符串
	 * @return 转换后的Float对象,转换失败返回null
	 */
	public static Float toFloat(String src) {
		try {
			return Float.parseFloat(src);
		} catch(Exception ex) {
			//转换失败
		}
		return null;
	}
	
	/**
	 * 字符串转Integer
	 * 例如: "123" -> 123
	 * 转换失败时返回null
	 * 
	 * @param src 源字符串
	 * @return 转换后的Integer对象,转换失败返回null
	 */
	public static Integer toInt(String src) {
		try {
			return Integer.parseInt(src);
		} catch(Exception ex) {
			//转换失败
		}
		return null;
	}
	
	/**
	 * 字符串转Long
	 * 例如: "123456789" -> 123456789L
	 * 转换失败时返回null
	 * 
	 * @param src 源字符串
	 * @return 转换后的Long对象,转换失败返回null
	 */
	public static Long toLong(String src) {
		try {
			return Long.parseLong(src);
		} catch(Exception ex) {
			//转换失败
		}
		return null;
	}
	
	/**
	 * 字符串转Boolean
	 * <p>
	 * 	当字符串为"true"或者"1"时返回true
	 * 	当字符串为"false"或者"0"时返回false
	 * 	其余情况返回null
	 * </p>
	 * 例如:
	 * "true" -> true
	 * "1" -> true
	 * "false" -> false
	 * "0" -> false
	 * "other" -> null
	 * 
	 * @param src 源字符串
	 * @return 转换后的Boolean对象,不匹配的情况返回null
	 */
	public static Boolean toBoolean(String src) {
		if(src != null) {
			if("true".equals(src) || "1".equals(src)) {
				return true;
			} else if("false".equals(src) || "0".equals(src)) {
				return false;
			}
		}
		
		return null;
	}

	/**
	 * 字符串转Boolean,提供默认值
	 * 当转换结果为null时返回默认值
	 * 
	 * @param src 源字符串
	 * @param defaultValue 默认值
	 * @return 转换后的boolean值,转换失败时返回默认值
	 */
	public static boolean toBoolean(String src, boolean defaultValue) {
		return ObjectUtils.getOrDefault(toBoolean(src), defaultValue);
	}
	
	/**
	 * 字符串转java.util.Date
	 * <p>
	 * 	支持六种格式
	 *	yyyy-MM-dd HH:mm:ss
	 *	yyyy-MM-dd HH:mm
	 * 	yyyy-MM-dd
	 * 	HH:mm:ss
	 * 13位数字
	 * 10位数字(不包含毫秒数，很多api都这么存，为了不超过Integer类型的最大值2147483647)
	 * 
	 * 值得注意的是:
	 * 	根据函数System.out.println(
				DateUtils.toString(Integer.MAX_VALUE*1000l, DateUtils.DEFAULT_TEMPLATE_TIME));
				计算得出所有用int类型接收10位时间戳的程序都将在2038-01-19 11:14:07后报错,尽量用long型接收
	 * </p>
	 * 
	 * @param src 需要转换的日期字符串
	 * @return 转换后的Date对象,转换失败返回null
	 */
	public static Date toDate(String src) {
		if(isEmpty(src)) {
			return null;
		}
		if(src.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")) {	//yyyy-MM-dd HH:mm:ss
			return DateUtils.toDate(src, "yyyy-MM-dd HH:mm:ss");
		} else if(src.matches("\\d{4}-\\d{2}-\\d{2}")) {		//yyyy-MM-dd
			return DateUtils.toDate(src, "yyyy-MM-dd");
		} else if(src.matches("\\d{13}")) {		//13位标准时间戳
			Long timeMillis = toLong(src);
			if (timeMillis == null) {
				return null;
			}
			return new Date(timeMillis);
		} else if(src.matches("\\d{10}")) {		//10位标准时间戳
			Long timeMillis = toLong(src);
			if (timeMillis == null) {
				return null;
			}
			return new Date(timeMillis*1000);
		} else if(src.matches("\\d{2}:\\d{2}:\\d{2}")) {	//HH:mm:ss
			return DateUtils.toDate(src, "HH:mm:ss");
		} else if(src.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}")) {	//yyyy-MM-dd HH:mm
			return DateUtils.toDate(src, "yyyy-MM-dd HH:mm");
		}
		return null;
	}
	

	
	/**
	 * 汉字转Unicode字符串
	 * @param src 需要转换的汉字字符串
	 * @return 转换后的Unicode字符串,输入为null时返回null
	 */
	public static String toUnicode(String src) {
		if(src == null) {
			return null;
		}
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);  // 取出每一个字符
            unicode.append("\\u").append(Integer.toHexString(c));// 转换为unicode
        }
        return unicode.toString();
    }
	
	/**
	 * unicode字符串转汉字
	 * <p>
	 * 通过\\\\u分隔
	 * </p>
	 * 
	 * @param unicode 需要转换的Unicode字符串
	 * @return 转换后的汉字字符串,输入为null时返回null
	 */
	public static String unicode2String(String unicode) {
		if(unicode == null) {
			return null;
		}
    	/* 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
          returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }
	
	/**
	 * 将字符串a重复times次
	 * <p><pre>{@code
	 * 	比如stack("0", 3)=>"000"
	 * 函数命名参考游戏minecraft创世神插件的函数名
	 * }</pre>
	 * 
	 * @param src 需要重复的源字符串
	 * @param times 重复的次数
	 * @return 重复后的字符串。当times小于0时返回空字符串
	 */
	public static String repeat(String src, int times) {
        return String.valueOf(src).repeat(Math.max(0, times));
	}

}