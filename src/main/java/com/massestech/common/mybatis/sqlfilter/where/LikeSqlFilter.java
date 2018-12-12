package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * like
 */
@Data
public class LikeSqlFilter extends AbstractSqlFilter {

    // java字段名
    public String fieldName;

    public LikeSqlFilter(String property, Object value) {
        super(property, value);
    }

    @Override
    protected void appendSql() {
        sb.append(this.getColumnName()).append(SqlCons.LIKE).append(this.getValue());
    }

}
