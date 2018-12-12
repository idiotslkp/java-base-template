package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.domain.BaseEntity;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.Map;

/**
 * sql条件过滤，空的数据不会加进去
 */
public class SqlFilterBuilder {

    private SqlFilterBuilder() {
    }

    /**
     * 创建字段SqlFilterHelper
     * @param baseEntity
     * @return
     */
    public static <T extends BaseEntity> ColumnSqlFilterBuilder buildColumn(T baseEntity) {
        setSqlFilterAdapter(baseEntity);

        ColumnSqlFilterBuilder columnSqlFilterBuilder = new ColumnSqlFilterBuilder(baseEntity);
        return columnSqlFilterBuilder;
    }

    /**
     * 根据class创建字段SqlFilterHelper
     * @param baseEntityClass
     * @return
     */
    public static ColumnSqlFilterBuilder buildColumn(Class<? extends BaseEntity> baseEntityClass) {
        BaseEntity baseEntity = BeanUtils.instantiate(baseEntityClass);
        return buildColumn(baseEntity);
    }

    /**
     * 创建查询SqlFilterHelper
     * @return
     */
    public static <T extends BaseEntity> WhereSqlFilterBuilder buildWhere(T baseEntity) {
        setSqlFilterAdapter(baseEntity);

        WhereSqlFilterBuilder whereSqlFilterBuilder = new WhereSqlFilterBuilder(baseEntity);
        return whereSqlFilterBuilder;
    }

    /**
     * 根据class创建查询SqlFilterHelper
     * @param baseEntityClass
     * @return
     */
    public static WhereSqlFilterBuilder buildWhere(Class<? extends BaseEntity> baseEntityClass) {
        BaseEntity baseEntity = BeanUtils.instantiate(baseEntityClass);
        return buildWhere(baseEntity);
    }

    /**
     * 创建连接SqlFilterHelper
     * @return
     */
    public static <T extends BaseEntity> JoinSqlFilterBuilder buildLeftJoin(T baseEntity) {
        setSqlFilterAdapter(baseEntity);

        JoinSqlFilterBuilder joinSqlFilterBuilder = new JoinSqlFilterBuilder(baseEntity);
        return joinSqlFilterBuilder;
    }

    static void setSqlFilterAdapter(BaseEntity baseEntity) {
        if (null == baseEntity) {
            throw new NullPointerException("baseEntity对象不能为null.");
        }
        SqlFilterAdapter sqlFilterAdapter = new SqlFilterAdapter();
        baseEntity.setSqlFilterAdapter(sqlFilterAdapter);
    }

    public static boolean isEmpty(Object o) throws IllegalArgumentException {
        if (o == null) {
            return true;
        }
        if (o instanceof String) {
            if (((String) o).trim().length() == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof Collection) {
            if (((Collection) o).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }


}
