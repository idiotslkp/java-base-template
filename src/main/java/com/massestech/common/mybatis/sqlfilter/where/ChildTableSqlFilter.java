package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 子查询
 */
public class ChildTableSqlFilter extends AbstractSqlFilter {

    /** 定义连接符，in 或者 = */
    private String joinString;

    /**
     * property in ( sqlFilter.sql() )   or  property = ( sqlFilter.sql() )
     * @param property
     * @param joinString in 或者 =
     * @param sqlFilter select column from table where 条件.
     */
    public ChildTableSqlFilter(String property, String joinString, AbstractSqlFilter sqlFilter) {
        this.property = property;
        this.joinString = joinString;
        this.value = sqlFilter;
        sb = new StringBuilder();
    }

    @Override
    protected void appendSql() {
        if (value instanceof AbstractSqlFilter) {
            AbstractSqlFilter sqlFilter = (AbstractSqlFilter) value;
            sb.append(this.getColumnName()).append(joinString).append("(").append(sqlFilter.sql()).append(")");
        } else {
            throw new RuntimeException("value的值必须是:" + AbstractSqlFilter.class + " 类型");
        }
    }

}
