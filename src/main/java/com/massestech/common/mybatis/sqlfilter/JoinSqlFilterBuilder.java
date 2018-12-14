package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.join.JoinMainTableSqlFilter;
import com.massestech.common.mybatis.sqlfilter.join.LeftJoibSqlFilter;

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
//        this.joinFilterList = new ArrayList<>();
    }

    //        // 构建查询条件
//        Query query = SqlFilterBuilder.buildTable("主表model").leftJoin("子表model").on("条件").relationId("关联id")
//              .leftJoin("子表model").on("条件").relationId("关联id");
//        Map map = mapper查询;
//        // 在处理数据的时候顺便根据query的条件去进行count查询,然后封装到page里面
//        Page<Model> page = util.dealData(query, map);

    public JoinSqlFilterBuilder test() {
        return this;
    }

    public LeftJoibSqlFilter leftJoin(Class<? extends BaseEntity> baseEntityClass) {
        LeftJoibSqlFilter leftJoibSqlFilter = new LeftJoibSqlFilter(baseEntityClass, this, null);
        this.joinMainTableSqlFilter.addJoinSqlFilter(leftJoibSqlFilter);
        return leftJoibSqlFilter;
    }

    public LeftJoibSqlFilter leftJoin(Class<? extends BaseEntity> baseEntityClass, String alias) {
        LeftJoibSqlFilter leftJoibSqlFilter = new LeftJoibSqlFilter(baseEntityClass, this, alias);
        this.joinMainTableSqlFilter.addJoinSqlFilter(leftJoibSqlFilter);
        return leftJoibSqlFilter;
    }


}

