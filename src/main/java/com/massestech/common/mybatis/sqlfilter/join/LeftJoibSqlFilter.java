package com.massestech.common.mybatis.sqlfilter.join;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.JoinSqlFilterBuilder;
import com.massestech.common.mybatis.utils.CamelToUnderline;
import com.massestech.common.mybatis.utils.SqlUtil;

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
    /** 关联字段,用于后续封装结果集的时候判断是否需要封装成嵌套的model */
    private String relationId;
    /** 是否是嵌套对象 */
    private boolean nesty;
    /** 用于返回语句构建器 */
    private JoinSqlFilterBuilder joinSqlFilterBuilder;

    private Map<String, String> columnAndFieldMap;

    public LeftJoibSqlFilter(Class<? extends BaseEntity> joinEntityClass, JoinSqlFilterBuilder joinSqlFilterBuilder, String alias, boolean nestyEntity) {
        if (alias == null) {
            nesty = false;
            this.alias = "_" + SqlUtil.tableName(joinEntityClass);
        } else {
            nesty = true;
            this.alias = "_" + alias;
        }
        this.joinEntityClass = joinEntityClass;
        this.joinSqlFilterBuilder = joinSqlFilterBuilder;
        this.columnAndFieldMap = SqlUtil.getColumnAndFieldsMap(joinEntityClass);
    }

    @Override
    protected void appendSql() {
        // 如果没有关联id，那么说明返回的结果集是直接封装在主model里面，否则就是封装在嵌套的model里面
        if (nesty) {
            sb = new StringBuilder();
            sb.append(",").append(alias).append(".").append(SqlCons.ID).append(" ").append(alias).append("_").append(SqlCons.ID);
            for (String column : columnAndFieldMap.keySet()) {
                sb.append(",").append(alias).append(".").append(column).append(" ")
                        .append(alias).append("_").append(columnAndFieldMap.get(column));
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
        onCondition.put(mainTableColumn, CamelToUnderline.camelToUnderline(leftJoinTableColumn));
        return this;
    }

    /**
     * 定义返回的值，field的字段名必须得是joinEntityClass里面的字段,如果不是，那么就使用下面的带有tableColumn参数的方法
     *
     * @param field model的字段名
     * @return
     */
    public LeftJoibSqlFilter field(String field) {
        sb.append(",").append(alias).append(".").append(CamelToUnderline.camelToUnderline(field)).append(" ").append(field);
        return this;
    }

    /**
     * 定义返回的值
     *
     * @param tableColumn 数据库的字段名
     * @param field model的字段名
     * @return
     */
    public LeftJoibSqlFilter columnAndField(String tableColumn, String field) {
        sb.append(",").append(alias).append(".").append(CamelToUnderline.camelToUnderline(tableColumn)).append(" ").append(field);
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

    /**
     * 拼接on的条件
     */
    public void appendOnCondition(StringBuilder sqlBuilder) {
        // 先拼接删除标识的sql
        sqlBuilder.append(SqlCons.ON).append(alias).append(".").append(SqlCons.DELETE_SQL);
        // 获取on的条件集合
        Map<String, String> onCondition = this.getOnCondition();
        // 遍历拼接on条件，a.id = b.parentId
        for (String mainTableColumn : onCondition.keySet()) {
            String leftJoinTableColumn = onCondition.get(mainTableColumn);
            sqlBuilder.append(SqlCons.AND).append(" a.").append(mainTableColumn).append("=").append(alias).append(".").append(leftJoinTableColumn);
        }
    }

    public boolean isNesty() {
        return nesty;
    }

    // --------------------------------------------以下是用于返回model里面包含model的参数封装的时候做映射关系用.----------------------------------------------------------------------

    /**
     * 设置关联的条件,并返回对应的条件构造器，如果relationId不为空，那么说明是一对多
     *
     * @param relationId
     */
    public JoinSqlFilterBuilder relationId(String relationId) {
        this.relationId = this.alias + "_" + relationId;
        return joinSqlFilterBuilder;
    }

    public String getRelationId() {
        return relationId;
    }

}
