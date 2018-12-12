package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * property = value
 */
public class EqualSqlFilter extends AbstractSqlFilter {

    public EqualSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append(SqlCons.EQ).append(this.getValue());
    }

}
