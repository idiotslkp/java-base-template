package com.massestech.common.mybatis.utils;


import com.massestech.common.domain.BaseEntity;
import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.mapper.MapperAware;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
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
    public static Map<Class<? extends EntityAware>, Map<String, String>> columnAndFieldsMap = new ConcurrentHashMap();
    /** 实体对应表名 */
    public static Map<Class<? extends EntityAware>, String> tableNameMap = new ConcurrentHashMap();


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

    /**
     * 根据calss获取class里面的字段名称以及对应的数据库字段名称的map集合.
     * @param entityClass
     * @return
     */
    public static Map<String, String> getColumnAndFieldsMap(Class<? extends EntityAware> entityClass) {
        setColumnList(entityClass);    // 初始化列信息
        return columnAndFieldsMap.get(entityClass);
    }


    /**
     * 用于计算列,这里只计算所有column的,子类可以做增强
     * @param entityClass
     */
    public static void setColumnList(Class<? extends EntityAware> entityClass) {

        if (!columnAndFieldsMap.containsKey(entityClass)) {
            Map<String, String> columnAndFieldMap = new HashMap();
            Map<String, String> fieldAndFieldTypeMap = new HashMap();

            String fieldName = null;

            for(Class clazz = entityClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    if (clazz != BaseEntity.class) {
                        Field[] declaredFields = clazz.getDeclaredFields();
                        int length = declaredFields.length;

                        for(int i = 0; i < length; ++i) {
                            Field field = declaredFields[i];
                            fieldName = field.getName();
                            /*
                            这里判断一下，如果是数据库类型字段，那么就保存在columnAndFieldMap之中，用于新增或者查询使用。
                            如果不是，那么就保存在fieldAndFieldTypeIsListMap之中，用于后续如果有连表查询的时候使用。
                             */
                            if (field.isAnnotationPresent(Column.class)) {
                                Column column = field.getAnnotation(Column.class);
                                String columnName = !"".equals(column.name()) ? column.name() : CamelToUnderline.camelToUnderline(fieldName);
                                columnAndFieldMap.put(columnName, fieldName);
                            } else {
                                String typeName = field.getGenericType().getTypeName();
                                if (typeName.contains("java.util.List")) {
                                    fieldAndFieldTypeMap.put(fieldName, null);
                                } else {
                                    fieldAndFieldTypeMap.put(fieldName, typeName);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
            columnAndFieldsMap.put(entityClass, columnAndFieldMap);
        }
    }

    /**
     * 根据实体class获取表名
     *
     * @param entityClass
     * @return
     */
    public static String tableName(Class entityClass) {
        // 先从缓存中获取表明，如果没有再根据反射去获取表名
        String tableName = tableNameMap.get(entityClass);
        if (tableName == null) {
            Table table = (Table) entityClass.getAnnotation(Table.class);
            if (table != null) {
                tableName = table.name();
                tableNameMap.put(entityClass, tableName);
            } else {
                throw new IllegalArgumentException("Undefine POJO @Table, need Annotation(@Table(name))");
            }
        }
        return tableName;
    }
}

