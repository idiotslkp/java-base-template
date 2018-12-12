package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.mybatis.utils.CamelToUnderline;
import lombok.Data;

/**
 * 基础的过滤器.
 */
@Data
public abstract class AbstractSqlFilter implements SqlFilter {

    protected String property;
    protected Object value;
    /** 用于保存拼接的sql */
    protected StringBuilder sb = new StringBuilder();

    public AbstractSqlFilter(){}

    public AbstractSqlFilter(String property, Object value) {
        this.property = property;
        this.value = value;
        this.sb = new StringBuilder();
    }

    protected String getColumnName() {
        return CamelToUnderline.camelToUnderline(this.getProperty());
    }

    /**
     * 拼接sql
     */
    protected abstract void appendSql();

    @Override
    public String sql() {
        if (sb.length() == 0) {
            appendSql();    // 拼接sql
        }
        return sb.toString();
    }
}
