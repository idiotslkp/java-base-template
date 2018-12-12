package com.massestech.common.mybatis.provider;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.provider.base.AbstractInsertTemplate;

public class InsertTemplate<T extends BaseEntity> extends AbstractInsertTemplate<T> {

    @Override
    protected void postProcessInsertSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
        tabColumnSb.append(this.insertColumn());
        columnValueSb.append(this.insertValue());
    }

    @Override
    protected void postProcessInsertBatchSql(T obj, StringBuilder tabColumnSb, StringBuilder columnValueSb) {
        tabColumnSb.append(this.insertColumn());
        columnValueSb.append(this.insertValue());
    }

    /**
     * 补充需要新增的列
     * @return
     */
    private String insertColumn() {
        StringBuilder sb = new StringBuilder();
        sb.append(",created_time,last_update_time,deleted");
        return sb.toString();
    }

    /**
     * 补充需要新增的列的值
     * @return
     */
    private String insertValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(",now(),now(),0");

        return sb.toString();
    }

}
