package com.massestech.common.mybatis.sqlfilter.join;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.JoinSqlFilterBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 左连接查询
 */
public class LeftJoibSqlFilter extends AbstractSqlFilter {

    private BaseEntity joinEntity;

    private Map<String, String> onCondition = new LinkedHashMap<>();
    /** 关联字段 */
    private String relationId;

    private JoinSqlFilterBuilder joinSqlFilterBuilder;

    //
    public LeftJoibSqlFilter(BaseEntity joinEntity, JoinSqlFilterBuilder joinSqlFilterBuilder) {
//        , JoinSqlFilterBuilder joinSqlFilterBuilder
        this.joinEntity = joinEntity;
        this.joinSqlFilterBuilder = joinSqlFilterBuilder;
    }

    @Override
    protected void appendSql() {
//        sb.append(SqlCons.SELECT).append("a.*,b.*").append(" left join ").append("").append(" on ");
    }

    public Map<String, String> getOnCondition() {
        return onCondition;
    }

    public BaseEntity getJoinEntity() {
        return joinEntity;
    }

    /**
     * 添加on条件
     * @param mainTableColumn
     * @param leftJoinTableColumn
     */
    public LeftJoibSqlFilter on(String mainTableColumn, String leftJoinTableColumn) {
        onCondition.put(mainTableColumn, leftJoinTableColumn);
        return this;
    }

    /**
     * 设置关联的条件,并返回对应的条件构造器
     * @param relationId
     */
    public JoinSqlFilterBuilder relationId(String relationId) {
        this.relationId = relationId;
        return joinSqlFilterBuilder;
    }

    // select a.* from (select * from tableA where condition = #{condition} limit ? ,?) a left join tableB b on a.id = b.parentId
    // a.*,b.*由他们自己的sqlFilter提供。这个filter只负责拼接。
    // select a.*,b.* from
    // select * from tableA where condition = #{condition} limit ? ,? --》 主表sqlFilter
    // tableB --》 关联表sqlFilter
    //



}
