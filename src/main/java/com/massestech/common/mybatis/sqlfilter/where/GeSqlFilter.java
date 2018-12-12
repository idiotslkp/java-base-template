package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 大于等于
 */
public class GeSqlFilter extends AbstractSqlFilter {

    public GeSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append(">=").append(this.getValue());
    }

}
