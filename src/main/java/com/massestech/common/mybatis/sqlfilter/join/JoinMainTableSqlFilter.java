package com.massestech.common.mybatis.sqlfilter.join;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.provider.SelectTemplate;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.SqlFilterBuilder;
import com.massestech.common.mybatis.utils.SqlUtil;
import com.massestech.javabasetemplate.domain.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 主表sqlFilter，负责生成 select column from table where condition = ? limit ?,?
 */
public class JoinMainTableSqlFilter extends AbstractSqlFilter {

    private BaseEntity baseEntity;

    /** 需要连接的表集合 */
    private List<LeftJoibSqlFilter> leftJoibSqlFilterList = new ArrayList<>();
    /** 分页 */
    private String pageSql = "";

    /**
     *
     * @param baseEntity
     */
    public JoinMainTableSqlFilter(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    //        // 构建查询条件
//        Query query = SqlFilterBuilder.buildTable("主表model").leftJoin("子表model").on("条件").relationId("关联id")
//              .leftJoin("子表model").on("条件").relationId("关联id");
//        Map map = mapper查询;
//        // 在处理数据的时候顺便根据query的条件去进行count查询,然后封装到page里面
//        Page<Model> page = util.dealData(query, map);
//    select a.*,b.* from (select * from tableA where condition = #{condition} limit ? ,?) a left join tableB b on a.id = b.parentId
    @Override
    protected void appendSql() {
        // 得到对应的主表的sql
        SelectTemplate selectTemplate = new SelectTemplate();
        String mainTableSql = selectTemplate.find(baseEntity);
        sb.append(SqlCons.SELECT).append(" a.*");
        // 定义当前sb的长度，后续要从这个地方将对应的连接表字段给加进去.
        int offset = sb.length();
        // 拼接主表
        sb.append(" from (").append(mainTableSql).append(this.pageSql).append(") a");
        StringBuilder leftJoinColumnBuilder = new StringBuilder();
        // 拼接子表
        for (int i = 0; i < leftJoibSqlFilterList.size(); i++) {
            LeftJoibSqlFilter leftJoibSqlFilter = leftJoibSqlFilterList.get(i);
            Class<? extends BaseEntity> joinEntityClass = leftJoibSqlFilter.getJoinEntityClass();
            String tableName = SqlUtil.tableName(joinEntityClass);
            // b.column1, b.column2
            // 拼接连接表的字段
            leftJoinColumnBuilder.append(leftJoibSqlFilter.sql());

            // left join tableB b
            sb.append(SqlCons.LEFT_JOIN)
                    .append(tableName).append(" ").append(leftJoibSqlFilter.getAlias());
            // on a.id = b.parentId
            leftJoibSqlFilter.appendOnCondition(sb);
        }
        sb.insert(offset, leftJoinColumnBuilder);

    }

    /**
     * 新增连接
     * @param leftJoibSqlFilter
     */
    public void addJoinSqlFilter(LeftJoibSqlFilter leftJoibSqlFilter) {
        leftJoibSqlFilterList.add(leftJoibSqlFilter);
    }

    /**
     * 获取连接sqlFilter
     * @return
     */
    public List<LeftJoibSqlFilter> getLeftJoibSqlFilterList() {
        return leftJoibSqlFilterList;
    }

    /**
     * 分页
     * @param pageNum 页数
     * @param pageSize 行数
     */
    public void pageSql(int pageNum, int pageSize) {
        this.pageSql = " limit " + pageNum + "," + pageSize;
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public static void main(String[] args) {
        // 这两个条件会自动的在sql里面加上
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("test");
        userEntity.setAge(20);

        SqlFilterBuilder.buildWhere(userEntity).like("userName", "test").leftJoinFilter().page(1, 10)
                .leftJoin(UserEntity.class).on("id", "parentId").field("age").columnAndField("user_name", "userName");
//                .relationId("parentId");
//            .leftJoin(UserEntity.class).on("id", "parentId").on("id", "parentId").relationId("parentId");

        SelectTemplate<UserEntity> selectTemplate = new SelectTemplate();
        String sql = selectTemplate.leftJoin(userEntity);
        System.out.println(sql);

    }

}
