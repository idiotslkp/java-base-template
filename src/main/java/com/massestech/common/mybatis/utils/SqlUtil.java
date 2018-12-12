package com.massestech.common.mybatis.utils;


import com.massestech.common.domain.BaseEntity;
import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.mapper.MapperAware;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql辅助相关工具类.
 */
public class SqlUtil {
    private static String badParam = "drop|delete|update |truncate|table|--";
    private static String badSql = "drop|delete|update |truncate|--";

    /** mapper对应实体的map */
    public static Map<Class<? extends MapperAware>, Class<? extends EntityAware>> entityMap = new ConcurrentHashMap<>();
    /** 实体对应属性列表的map */
    public static Map<Class<? extends EntityAware>, List<Map<String, String>>> mapColumns = new ConcurrentHashMap();
    /** 属性名 */
    public static String MODEL_ATTRIBUTE = "model_attribute";
    /** 列名 */
    public static String TAB_COLUMN = "tabColumn";
    /** 列值 */
    public static String COLUMN_VALUE = "columnValue";


    public SqlUtil() {
    }

    public static boolean isSpiteSql(String sql) {
        return sqlValidate(sql, badSql);
    }

    public static boolean isSpiteParams(String sqlParams) {
        return sqlValidate(sqlParams, badParam);
    }

    private static boolean sqlValidate(String sqlParams, String badStr) {
        boolean bool = true;
        if (StringUtils.isNotEmpty(sqlParams)) {
            String sqlLowerCase = sqlParams.toLowerCase();
            String[] badStrs = badStr.split("\\|");

            for(int i = 0; i < badStrs.length; ++i) {
                if (sqlLowerCase.indexOf(badStrs[i]) >= 0) {
                    return false;
                }
            }
        }

        return bool;
    }

    /**
     * 避免重复的%
     * @param value
     * @return
     */
    public static String likeValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        } else {
            int start = 0;
            int end = value.length();
            String startValue = "";
            String endValue = "";
            if ("%".equals(value.substring(0, 1))) {
                start = 1;
                startValue = "%";
            }

            if ("%".equals(value.substring(value.length() - 1, value.length()))) {
                end = value.length() - 1;
                endValue = "%";
            }

            String v = value.substring(start, end);
            if (v.indexOf("%") != -1) {
                value = startValue + v.replaceAll("%", "[%]") + endValue;
            }

            return value;
        }
    }

    public static List<Map<String, String>> getColumnList(Class<? extends EntityAware> entityClass) {
        setColumnList(entityClass);    // 初始化列信息
        return mapColumns.get(entityClass);
    }

    /**
     * 用于计算列,这里只计算所有column的,子类可以做增强
     * @param entityClass
     */
    public static void setColumnList(Class<? extends EntityAware> entityClass) {
        if (!mapColumns.containsKey(entityClass)) {
            List<Map<String, String>> columnList = new ArrayList();
            Map<String, String> map = null;
            String columnName = null;

            for(Class clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    if (clazz != BaseEntity.class) {
                        Field[] declaredFields = clazz.getDeclaredFields();
                        int length = declaredFields.length;

                        for(int i = 0; i < length; ++i) {
                            Field field = declaredFields[i];
                            if (field.isAnnotationPresent(Column.class)) {
                                Column column = field.getAnnotation(Column.class);
                                columnName = field.getName();
                                map = new HashMap();
                                map.put(MODEL_ATTRIBUTE, columnName);
                                map.put(TAB_COLUMN, !"".equals(column.name()) ? column.name() : CamelToUnderline.camelToUnderline(columnName));
                                columnList.add(map);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            mapColumns.put(entityClass, columnList);
        }
    }
}

