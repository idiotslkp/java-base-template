package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.join.JoinMainTableSqlFilter;
import com.massestech.common.mybatis.sqlfilter.join.LeftJoibSqlFilter;
import com.massestech.common.web.PageInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理连接查询
 */
public class JoinSqlFilterBuilder  {

    private BaseEntity entity;

    private JoinMainTableSqlFilter joinMainTableSqlFilter;

    /**
     * 构造左链接，然后
     * @param entity
     */
    public JoinSqlFilterBuilder(BaseEntity entity) {
        this.entity = entity;
        this.joinMainTableSqlFilter = new JoinMainTableSqlFilter(entity);
        this.entity.getSqlFilterAdapter().setJoinSqlFilter(joinMainTableSqlFilter);
    }

    //        // 构建查询条件
//        Query query = SqlFilterBuilder.buildTable("主表model").leftJoin("子表model").on("条件").relationId("关联id")
//              .leftJoin("子表model").on("条件").relationId("关联id");
//        Map map = mapper查询;
//        // 在处理数据的时候顺便根据query的条件去进行count查询,然后封装到page里面
//        Page<Model> page = util.dealData(query, map);

    /**
     * 分页
     * @param pageInfo
     * @return
     */
    public JoinSqlFilterBuilder page(PageInfoView pageInfo) {
        page(pageInfo.getPageNum(), pageInfo.getPageNum());
        return this;
    }

    /**
     * 分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public JoinSqlFilterBuilder page(int pageNum, int pageSize) {
        this.joinMainTableSqlFilter.pageSql(pageNum, pageSize);
        return this;
    }

    /**
     * 左连接查询
     * @param joinEntityClass
     * @return
     */
    public LeftJoibSqlFilter leftJoin(Class<? extends BaseEntity> joinEntityClass) {
        LeftJoibSqlFilter leftJoibSqlFilter = new LeftJoibSqlFilter(joinEntityClass, this, null, false);
        this.joinMainTableSqlFilter.addJoinSqlFilter(leftJoibSqlFilter);
        return leftJoibSqlFilter;
    }

    /**
     * 嵌套的连接
     * @param joinEntityClass
     * @param alias joinEntityClass在主model中的名称.
     * @return
     */
    public LeftJoibSqlFilter leftJoin(Class<? extends BaseEntity> joinEntityClass, String alias) {
        LeftJoibSqlFilter leftJoibSqlFilter = new LeftJoibSqlFilter(joinEntityClass, this, alias, true);
        this.joinMainTableSqlFilter.addJoinSqlFilter(leftJoibSqlFilter);
        return leftJoibSqlFilter;
    }

}

