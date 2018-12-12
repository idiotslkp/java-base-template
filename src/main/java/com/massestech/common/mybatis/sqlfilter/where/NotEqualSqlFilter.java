package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 不等于
 */
public class NotEqualSqlFilter extends AbstractSqlFilter {

    public NotEqualSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append("<>").append(this.getValue());
    }

}
