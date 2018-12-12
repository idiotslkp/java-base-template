package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 大于(greater than)
 */
public class GtSqlFilter extends AbstractSqlFilter {

    public GtSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append(">").append(this.getValue());
    }

}
