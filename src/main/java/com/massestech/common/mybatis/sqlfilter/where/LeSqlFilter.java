package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 小于等于
 */
public class LeSqlFilter extends AbstractSqlFilter {

    public LeSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append("<=").append(this.getValue());
    }

}
