package com.massestech.common.mybatis.sqlfilter.columu;

import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * 求和
 */
public class SumSqlFilter extends AbstractSqlFilter {

    public SumSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        // ifnull(sum(property) as value
        sb.append("ifnull(sum(")
                .append(this.getColumnName())    // 驼峰转下划线.
                .append("), 0) ");
        if (StringUtils.isNotEmpty(String.valueOf(value))) {
            sb.append(" as ").append(this.getValue());
        }

    }

}
