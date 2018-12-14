package com.massestech.common.mybatis.sqlfilter.join;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.provider.SelectTemplate;
import com.massestech.common.mybatis.sqlfilter.AbstractSqlFilter;
import com.massestech.common.mybatis.sqlfilter.SqlFilterBuilder;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import com.massestech.javabasetemplate.domain.UserEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 主表sqlFilter，负责生成 select column from table where condition = ? limit ?,?
 */
public class JoinMainTableSqlFilter extends AbstractSqlFilter {

    private BaseEntity baseEntity;

    /** 需要连接的表集合 */
    private List<LeftJoibSqlFilter> leftJoibSqlFilterList = new ArrayList<>();

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

//        SELECT id,user_name,age,sex_status,created_time,last_update_time
//        FROM user
//        WHERE ( deleted=0)

//        SELECT id,user_name,age,sex_status,created_time,last_update_time
//        FROM user
//        WHERE ( deleted=0)
//        ORDER BY created_time desc
        // 得到对应的主表的sql
        SelectTemplate selectTemplate = new SelectTemplate();
        String mainTableSql = selectTemplate.findList(baseEntity);
        sb.append(SqlCons.SELECT).append(" a.*");
        // 定义当前sb的长度，后续要从这个地方将对应的连接表字段给加进去.
        int offset = sb.length();
        // 拼接子表b.*
//        sb.
        // 拼接主表
        sb.append(" from (").append(mainTableSql).append(") a");
        StringBuilder leftJoinColumnBuilder = new StringBuilder();
        // 拼接子表
        for (int i = 0; i < leftJoibSqlFilterList.size(); i++) {
            LeftJoibSqlFilter leftJoibSqlFilter = leftJoibSqlFilterList.get(i);
            Class<? extends BaseEntity> joinEntityClass = leftJoibSqlFilter.getJoinEntityClass();
            String tableName = SqlUtil.tableName(joinEntityClass);
            // b.column1, b.column2
            // 拼接连接表的字段
//            appendLeftJoinCloumn(leftJoinColumnBuilder, joinEntityClass, tableName);
            leftJoinColumnBuilder.append(leftJoibSqlFilter.sql());

            // left join tableB b
            sb.append(SqlCons.LEFT_JOIN)
                    .append(tableName).append(" ").append(tableName)
            .append(SqlCons.ON);
            // on a.id = b.parentId
            appendOnCondition(leftJoibSqlFilter, tableName);
        }
        sb.insert(offset, leftJoinColumnBuilder);

//        System.out.println(mainTableSql);
    }

//    /**
//     * 拼接连接表的字段
//     * @param leftJoinColumnBuilder
//     * @param joinEntityClass 连接表的class
//     */
//    private void appendLeftJoinCloumn(StringBuilder leftJoinColumnBuilder, Class<? extends BaseEntity> joinEntityClass, String tableName) {
//        Iterator columnIterator = SqlUtil.getColumnList(joinEntityClass).iterator();
//        // 获取连接表的表字段,b.column1,b.column2,c.column1,c.column2
//        while(columnIterator.hasNext()) {
//            Map<String, String> map = (Map)columnIterator.next();
//            String joinColumnName = map.get(SqlUtil.TAB_COLUMN);
//            leftJoinColumnBuilder.append(",").append(tableName).append(".").append(joinColumnName);
//        }
//    }

    /**
     * 拼接on的条件
     * @param leftJoibSqlFilter
     * @param tableName
     */
    private void appendOnCondition(LeftJoibSqlFilter leftJoibSqlFilter, String tableName) {
        // 获取on的条件集合
        Map<String, String> onCondition = leftJoibSqlFilter.getOnCondition();
        // 是否是第一次增加on的条件，如果不是的话，那么需要加一个and
        boolean isFirstCondition = true;
        // 遍历拼接on条件，on a.id = b.parentId
        for (String mainTableColumn : onCondition.keySet()) {
            String leftJoinTableColumn = onCondition.get(mainTableColumn);
            if (!isFirstCondition) {
                sb.append(SqlCons.AND);
            } else {
                isFirstCondition = false;
            }
            sb.append(" a.").append(mainTableColumn).append("=").append(tableName).append(".").append(leftJoinTableColumn);
        }
    }

    public void addJoinSqlFilter(LeftJoibSqlFilter leftJoibSqlFilter) {
        leftJoibSqlFilterList.add(leftJoibSqlFilter);
    }

    public static void main(String[] args) {
        // 这两个条件会自动的在sql里面加上
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("test");
        userEntity.setAge(20);

        SqlFilterBuilder.buildWhere(userEntity).like("userName", "test").leftJoinFilter()
                .leftJoin(UserEntity.class).on("id", "parentId").relationId("parentId");
//            .leftJoin(UserEntity.class).on("id", "parentId").on("id", "parentId").relationId("parentId");

        SelectTemplate<UserEntity> selectTemplate = new SelectTemplate();
        String sql = selectTemplate.leftJoin(userEntity);
        System.out.println(sql);

    }

}
