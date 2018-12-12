package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 小于,property < value
 */
public class LtSqlFilter extends AbstractSqlFilter {

    public LtSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append("<").append(this.getValue());
    }

}
