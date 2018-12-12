package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;

/**
 * 最原始的sqlFilter,语句自己拼接.
 */
public class OriginalSqlFilter extends AbstractSqlFilter {

    public OriginalSqlFilter(String value) {
        super(null, value);
//        this.value = value;
//        this.sb = new StringBuilder();
    }

    @Override
    protected void appendSql() {
        sb.append(value);
    }

}
