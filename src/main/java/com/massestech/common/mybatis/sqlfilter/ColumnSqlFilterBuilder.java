package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.sqlfilter.columu.SumSqlFilter;

import java.util.ArrayList;
import java.util.List;

import static com.massestech.common.mybatis.sqlfilter.SqlFilterBuilder.isEmpty;

/**
 * 列属性
 */
public class ColumnSqlFilterBuilder {

    private BaseEntity entity;

    /** 查询的字段 */
    private List<SqlFilter> columnSqlFilterList;

    public ColumnSqlFilterBuilder(BaseEntity entity) {
        this.entity = entity;
        columnSqlFilterList = new ArrayList<>();
        this.entity.getSqlFilterAdapter().setColumnSqlFilterList(columnSqlFilterList);
    }

    public ColumnSqlFilterBuilder sum(String property) {
        if (isEmpty(property)) {
            throw new NullPointerException("sum的字段名不能为空.");
        }


        columnSqlFilterList.add(new SumSqlFilter(property, null));
        return this;
    }

    public ColumnSqlFilterBuilder sum(String property, Object value) {
        if (isEmpty(property)) {
            throw new NullPointerException("sum的字段名不能为空.");
        }

        if (isEmpty(value)) {
            throw new NullPointerException("sum的别名不能为空.");
        }

        columnSqlFilterList.add(new SumSqlFilter(property, value));
        return this;
    }

    /**
     * 得到WhereSqlFilterBuilder，以便于继续执行拼装sql
     * @return
     */
    public WhereSqlFilterBuilder whereFilter() {
        WhereSqlFilterBuilder whereSqlFilterBuilder = new WhereSqlFilterBuilder(entity);
        return whereSqlFilterBuilder;
    }

    public  <T> T getEntity() {
        return (T) entity;
    }

}
