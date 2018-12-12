package com.massestech.common.mybatis.provider.base;

import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.mapper.MapperAware;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;

import java.util.Iterator;
import java.util.Map;

/**
 * 基础的查询模板
 * @param <T>
 */
public abstract class AbstractSelectTemplate<T extends EntityAware> extends AbstractTemplate<T> {

    /**
     * 根據条件进行查询,返回单条
     */
    public String find(T obj) {
        SQL sql = getFindSql(obj);
        return sql.toString();
    }

    /**
     * 根据条件进行查询list
     */
    public String findList(T obj) {
        SQL sql = getFindSql(obj);
        // 排序
        String orderBy = processOrderBySql(obj);
        if (StringUtils.isNotEmpty(orderBy)) {
            sql.ORDER_BY(orderBy);
        }
        return sql.toString();
    }

    /**查询sql*/
    protected SQL getFindSql(T obj) {
        SQL sql = new SQL();
        String column = selectColumnSqlByObj(obj);
        sql.SELECT(column);
        sql.FROM(ReflectUtils.tableName(obj.getClass()));
        String where = whereSql(obj);
        sql.WHERE(where);
        return sql;
    }

    /**默认的空实现的排序*/
    protected String processOrderBySql(T obj) {
        return "";
    }
    /**查询全部的时候用到的排序*/
    protected String processOrderBySql() {
        return "";
    }

    public String findById(ProviderContext context) {
        SQL sql = getSelectSql(context);
        sql.WHERE(findByIdWhereSql()); // id的查询交由子类去实现
        return sql.toString();
    }

    /**根据id查询的条件*/
    protected abstract String findByIdWhereSql();

    public String findAll(ProviderContext context) {
        SQL sql = getSelectSql(context);
        String findAllWhereSql = noConditionWhereSql();
        if (StringUtils.isNotEmpty(findAllWhereSql)) {
            sql.WHERE( findAllWhereSql);
        }
        // 排序
        String orderBy = processOrderBySql();
        if (StringUtils.isNotEmpty(orderBy)) {
            sql.ORDER_BY(orderBy);
        }
        return sql.toString();
    }

    // 没有传参的时候的查询条件,软删除之类的用的到
    protected String noConditionWhereSql(){
        return "";
    }

    protected SQL getSelectSql(ProviderContext context) {
        Class<? extends EntityAware> entityClass = getEntityClass((Class<? extends MapperAware>) context.getMapperType());
        SQL sql = new SQL();
        String column = selectColumnSqlByClazz((Class<T>) entityClass);
        sql.SELECT(column);
        sql.FROM(ReflectUtils.tableName(entityClass));
        return sql;
    }

    public String count(ProviderContext context) {
        Class<? extends EntityAware> entityClass = getEntityClass((Class<? extends MapperAware>) context.getMapperType());

        SQL sql = new SQL();
        sql.SELECT("count(1)");
        sql.FROM(ReflectUtils.tableName(entityClass));
        String findAllWhereSql = this.noConditionWhereSql();
        if (StringUtils.isNotEmpty(findAllWhereSql)) {
            sql.WHERE( findAllWhereSql);
        }
        return sql.toString();
    }

    public String countCondition(T obj) {
        SQL sql = new SQL();
        sql.SELECT("count(1)");
        sql.FROM(ReflectUtils.tableName(obj.getClass()));
        String where = this.whereSql(obj);
        sql.WHERE(where);
        return sql.toString();
    }

    protected String selectColumnSqlByObj(T obj) {
        // 默认使用clazz的策略去生成sql
        return selectColumnSqlByClazz((Class<T>) obj.getClass());
    }

    protected String selectColumnSqlByClazz(Class<T> clazz) {
        StringBuilder tabColumnSb = new StringBuilder();

        processSelectColumnSql(clazz, tabColumnSb);

        postProcessSelectColumnSql(clazz, tabColumnSb);
        return tabColumnSb.toString();
    }

    protected void processSelectColumnSql(Class<T> clazz, StringBuilder tabColumnSb) {
        Iterator columnIterator = SqlUtil.getColumnList(clazz).iterator();
        while(columnIterator.hasNext()) {
            Map<String, String> map = (Map)columnIterator.next();
            if (tabColumnSb.length() > 0) {
                tabColumnSb.append(",");
            }
            tabColumnSb.append(map.get(SqlUtil.TAB_COLUMN));
        }
    }

    /**默认的空实现,给子类去进行增强*/
    protected void postProcessSelectColumnSql(Class<T> clazz, StringBuilder tabColumnSb){
    }

}
