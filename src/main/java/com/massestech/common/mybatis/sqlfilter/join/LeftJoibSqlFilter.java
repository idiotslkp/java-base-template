package com.massestech.common.mybatis.sqlfilter.join;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.JoinSqlFilterBuilder;
import com.massestech.common.mybatis.utils.CamelToUnderline;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 左连接查询
 */
public class LeftJoibSqlFilter extends AbstractSqlFilter {

    /** 连接表的别名,如果指定别名，那么说明该连接到时返回的参数会封装在嵌套的model里面. */
    private String alias;
    /** 连接的class */
    private Class<? extends BaseEntity> joinEntityClass;
    /** on连接条件集合 */
    private Map<String, String> onCondition = new LinkedHashMap<>();
    /** 关联字段 */
    private String relationId;
    /** 用于返回语句构建器 */
    private JoinSqlFilterBuilder joinSqlFilterBuilder;

    private Map<String, String> columnAndFieldMap;

    public LeftJoibSqlFilter(Class<? extends BaseEntity> joinEntityClass, JoinSqlFilterBuilder joinSqlFilterBuilder, String alias) {
        this.alias = alias == null ? SqlUtil.tableName(joinEntityClass) : alias;
        this.joinEntityClass = joinEntityClass;
        this.joinSqlFilterBuilder = joinSqlFilterBuilder;
        this.columnAndFieldMap = SqlUtil.getColumnAndFieldsMap(joinEntityClass);
    }

    @Override
    protected void appendSql() {
        // 如果没有关联id，那么说明返回的结果集是直接封装在主model里面，否则就是封装在嵌套的model里面
        if (null != relationId) {
            sb = new StringBuilder();
            for (String column : columnAndFieldMap.keySet()) {
                sb.append(",").append(alias).append(".").append(column).append(" ").append(columnAndFieldMap.get(column));
            }
        }
        if (sb.length() == 0) {
            // todo 异常.
        }
    }

    public Map<String, String> getOnCondition() {
        return onCondition;
    }

    public Class<? extends BaseEntity> getJoinEntityClass() {
        return joinEntityClass;
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
     * 定义返回的值，field的字段名必须得是joinEntityClass里面的字段,如果不是，那么就使用下面的带有tableColumn参数的方法
     *
     * @param field model的字段名
     * @return
     */
    public LeftJoibSqlFilter field(String field) {
        sb.append(",").append(CamelToUnderline.camelToUnderline(field)).append(" ").append(field);
        return this;
    }

    /**
     * 定义返回的值
     *
     * @param tableColumn 数据库的字段名
     * @param field model的字段名
     * @return
     */
    public LeftJoibSqlFilter field(String tableColumn, String field) {
        sb.append(",").append(CamelToUnderline.camelToUnderline(tableColumn)).append(" ").append(field);
        return this;
    }

    /**
     * 获取表别名
     *
     * @return
     */
    public String getAlias() {
        return alias;
    }

    // --------------------------------------------以下是用于返回model里面包含model的参数封装的时候做映射关系用.----------------------------------------------------------------------

    /**
     * 设置关联的条件,并返回对应的条件构造器
     * @param relationId
     */
    public JoinSqlFilterBuilder relationId(String relationId) {
        this.relationId = relationId;
        return joinSqlFilterBuilder;
    }

}
