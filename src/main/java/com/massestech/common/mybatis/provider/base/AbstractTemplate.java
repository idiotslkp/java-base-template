package com.massestech.common.mybatis.provider.base;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.mapper.MapperAware;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import com.massestech.common.mybatis.utils.CamelToUnderline;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供基础的sql模板
 * @param <T>
 */
public abstract class AbstractTemplate<T extends EntityAware> {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTemplate.class);

    /**
     * 根据mapperClass获取表名
     * @param mapperClass
     * @return
     */
    protected String tableNameByMapperClass(Class mapperClass) {
        return SqlUtil.tableName(getEntityClass(mapperClass));
    }

    /**
     * 根据mapperClass得到对应的泛型class
     * @param mapperClass
     * @return
     */
    public Class getEntityClass(Class<? extends MapperAware> mapperClass) {
        // 先到缓存的map里面去找,找不到的情况下,才根据反射去获取class
        Class<? extends EntityAware> entityClass = SqlUtil.entityMap.get(mapperClass);
        if (null == entityClass) {
            // 根据mapper得到entity的class
            entityClass = (Class<? extends EntityAware>) ReflectUtils.getEntityClass(mapperClass);
            // 根据
            SqlUtil.entityMap.put(mapperClass, entityClass);
        }
        return entityClass;
    }

    /**
     * 提供默认的where实现.这块后续要抽取出去
     * @param obj
     * @return
     */
    public String whereSql(T obj) {
        StringBuilder whereSb = new StringBuilder();
        preProcessWhereSql(obj, whereSb); // 前置处理
        processWhereSql(obj, whereSb);  // 这里面只会处理那些column的注解,并不会处理id的注解,需要自己在前置处理里面实现.
        postProcessWhereSql(obj, whereSb);    // 后置处理
        return whereSb.toString();
    }

    /**默认提供的空实现*/
    protected void preProcessWhereSql(T obj, StringBuilder whereSb) {
    }

    public void processWhereSql(T obj, StringBuilder whereSb) {
        String columns = ",";   // 该字段用来避免重复的字段.

        if (obj instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) obj;
            if (baseEntity.getSqlFilterAdapter() != null &&
            null != baseEntity.getSqlFilterAdapter().getWhereSqlFilterList()
                    && baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().size() > 0) {
                Iterator<SqlFilter> filterIteratort = baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().iterator();

                while(filterIteratort.hasNext()) {
                    SqlFilter filter = filterIteratort.next();
                    if (filter != null && !StringUtils.isEmpty(filter.sql())) {
                        if (!"".equals(whereSb.toString())) {
                            whereSb.append(" and ");
                        }
                        whereSb.append(filter.sql());
                        columns = columns + filter.getProperty() + ",";
                    }
                }
            }
        }
        // 列属性
        String col = "";
        Map<String, String> columnAndFieldsMap = SqlUtil.getColumnAndFieldsMap(obj.getClass());
        for (String column : columnAndFieldsMap.keySet()) {
            // 这里只加入那些属性值不为null的。
            if (!ReflectUtils.isNull(obj, columnAndFieldsMap.get(column))) {
                // 判断,如果sqlFilter里面对应的字段已经有了,那么entity里面的字段,就不拼接进去了.
                col = "," + columnAndFieldsMap.get(column) + ",";
                if (columns.indexOf(col) == -1) {
                    if (!"".equals(whereSb.toString())) {
                        whereSb.append(" and ");
                    }
                    whereSb.append(column).append("=#{").append(columnAndFieldsMap.get(column)).append("}");
                }
            }
        }
    }

    protected void postProcessWhereSql(T obj, StringBuilder whereSb) {
    }

}
