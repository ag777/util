package github.ag777.util.db;

import github.ag777.util.lang.ObjectUtils;
import github.ag777.util.lang.StringUtils;
import github.ag777.util.lang.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ag777 <837915770@vip.qq.com>
 * @version 2025/2/19 上午11:30
 */
public class ResultSets {


    public <T> List<T> toList(ResultSet rs, Class<T> clazz) throws IllegalAccessException, SQLException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        List<T> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount(); // Map rowData;
        String[] cols = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            cols[i-1] = StringUtils.underline2Camel(md.getColumnName(i), false);	//首字母大写，驼峰
        }
        while (rs.next()) { // rowData = new HashMap(columnCount);
            T rowData = ReflectionUtils.newInstance(clazz);
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if(field.getName().equalsIgnoreCase(cols[i-1])) {
                        // 类型转换
                        if (field.getType() == Boolean.class) {
                            value = ObjectUtils.toBoolean(value);
                        } else if (field.getType() == boolean.class) {
                            value = ObjectUtils.toBoolean(value);
                        } else if (field.getType() == Date.class) {
                            value = ObjectUtils.toDate(value);
                        }
                        // 设置字段
                        boolean flag = field.canAccess(null);
                        field.setAccessible(true);
                        field.set(rowData, value);
                        field.setAccessible(flag);
                    }
                }
            }
            list.add(rowData);
        }
        return list;
    }
}
