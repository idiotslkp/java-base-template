package com.massestech.common.mybatis.utils;

import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.mapper.MapperAware;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用于模板的辅助类
 */
public abstract class ReflectUtils {

    /**
     * 根据字段名判断对应的字段在对象里面是否为空
     * @param obj 判断的对象
     * @param fieldName 需要进行判断的字段
     * @return
     */
    public static <T extends EntityAware> boolean isNull(T obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj) == null;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 根据字段名判断对应的字段在对象里面是否为空
     * @param obj 判断的对象
     * @param fieldName 需要进行判断的字段
     * @return
     */
    public static <T extends EntityAware> Object getFieldValue(T obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            // todo 这里应该要报错才对.
            return null;
        }
    }

    /**
     * 根据mapper接口获取到mapper的泛型参数
     *
     * @param mapperClass
     * @return
     */
    public static Class<?> getEntityClass(Class<? extends MapperAware> mapperClass) {
        Class<?> entityClass = null;
        // 根据mapper得到entity的class
        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                // 得到泛型类型
                if (null != ((Class<?>) t.getActualTypeArguments()[0]).getAnnotation(Table.class)){
                    entityClass = (Class<?>) t.getActualTypeArguments()[0];
                }
                break;
            }
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("接口的泛型类型中,缺少@Table注解");
        }
        return entityClass;
    }

    /**
     * 根据实体class获取表名
     *
     * @param entityClass
     * @return
     */
    public static String tableName(Class entityClass) {
        Table table = (Table) entityClass.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        } else {
            throw new IllegalArgumentException("Undefine POJO @Table, need Annotation(@Table(name))");
        }
    }

}
