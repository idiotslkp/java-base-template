package com.massestech.common.mybatis.provider;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.provider.base.AbstractSelectTemplate;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import com.massestech.common.mybatis.utils.CamelToUnderline;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 查询
 * @param <T>
 */
public class SelectTemplate<T extends BaseEntity> extends AbstractSelectTemplate<T> {

    @Override
    protected void postProcessSelectColumnSql(Class<T> clazz, StringBuilder tabColumnSb) {
        tabColumnSb.insert(0, "id,");
        tabColumnSb.append(",created_time createdTime,last_update_time lastUpdateTime");
    }

    @Override
    protected void postProcessWhereSql(T obj, StringBuilder whereSb) {
        // 如果是空默认给他一个
        if (whereSb.length() == 0) {
            whereSb.append("deleted = 0");
        }
        if (null != obj.getId()) {
            whereSb.append(" and id=#{id}");
        }
    }

    @Override
    protected String processOrderBySql(T obj) {
        StringBuilder orderBySb = new StringBuilder();
        String oderColumn;
        if (!StringUtils.isEmpty(obj.getOrderByDesc())) {
            String[] orderColumns = obj.getOrderByDesc().split(",");

            for(int orderIndex = 0; orderIndex < orderColumns.length; ++orderIndex) {
                oderColumn = orderColumns[orderIndex];
                if (!"".equals(orderBySb.toString())) {
                    orderBySb.append(",");
                }

                orderBySb.append(CamelToUnderline.camelToUnderline(oderColumn)).append(" desc");
            }
        }

        if (!StringUtils.isEmpty(obj.getOrderBy())) {
            if (!StringUtils.isEmpty(orderBySb.toString())) {
                orderBySb.append(",");
            }

            String[] orderColumns = obj.getOrderBy().split(",");

            for(int orderIndex = 0; orderIndex < orderColumns.length; ++orderIndex) {
                oderColumn = orderColumns[orderIndex];
                if (!"".equals(orderBySb.toString())) {
                    orderBySb.append(",");
                }

                orderBySb.append(CamelToUnderline.camelToUnderline(oderColumn)).append(" asc");
            }
        }

        // 没有排序的情况下默认按照时间进行排序
        if ("".equals(orderBySb.toString())) {
            orderBySb.append("created_time").append(" desc");
        }
        return orderBySb.toString();
    }

    /**默认按照创建时间的倒序*/
    @Override
    protected String processOrderBySql() {
        return "created_time desc";
    }

    /**  */
    @Override
    protected String findByIdWhereSql() {
        return "id =#{id} and deleted = 0";
    }

    @Override
    protected String noConditionWhereSql() {
        return "deleted = 0";
    }

    @Override
    protected void preProcessWhereSql(T obj, StringBuilder whereSb) {
        // 查询条件判断是否已经包含了deleted=0.因为项目里面只需要查出还没有删掉的数据
        if (whereSb.indexOf("deleted") == -1) {
            if (!"".equals(whereSb.toString())) {
                whereSb.append(" and deleted=0");
            } else {
                whereSb.append(" deleted=0");
            }
        }
    }

    protected String selectColumnSqlByObj(T obj) {
        // 如果指定了自定义sql,那么就使用自定义sql的.
        if (null == obj.getSqlFilterAdapter()
                || CollectionUtils.isEmpty(obj.getSqlFilterAdapter().getColumnSqlFilterList())) {
            return super.selectColumnSqlByObj(obj);
        } else {
            List<SqlFilter> columnSqlFilterList = obj.getSqlFilterAdapter().getColumnSqlFilterList();
            StringBuilder tabColumnSb = new StringBuilder();
            // 迭代拼接sql
            for (int i = 0; i < columnSqlFilterList.size(); i++) {
                SqlFilter sqlFilter = columnSqlFilterList.get(i);
                if (i == 0) {
                    tabColumnSb.append(sqlFilter.sql());
                } else {
                    tabColumnSb.append(", ").append(sqlFilter.sql());
                }
            }
            return tabColumnSb.toString();
        }
    }

    /**
     * 左连接查询
     *
     * @param obj
     * @return
     */
    public String leftJoin(T obj) {
        // 语句的拼接不在这里进行，交给SqlFilterAdapter
        String sql = obj.getSqlFilterAdapter().getJoinSqlFilter().sql();
        return sql;
    }

}
