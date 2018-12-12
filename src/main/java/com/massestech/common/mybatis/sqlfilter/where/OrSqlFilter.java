package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;

/**
 * or过滤器
 */
public class OrSqlFilter extends AbstractSqlFilter {

    private SqlFilter sqlFilter;
    private SqlFilter sqlFilter1;

    public OrSqlFilter(String property, Object value) {
        super(property, value);
    }

    public OrSqlFilter(SqlFilter sqlFilter, SqlFilter sqlFilter1) {
        this.sqlFilter = sqlFilter;
        this.sqlFilter1 = sqlFilter1;
        sb = new StringBuilder();
    }

    @Override
    protected void appendSql() {
        // select * from table where id = xxx and (name like %lkp% or name like %shuai%)
        sb.append("(").append(sqlFilter.sql()).append(SqlCons.OR).append(sqlFilter1.sql()).append(")");

    }
}
