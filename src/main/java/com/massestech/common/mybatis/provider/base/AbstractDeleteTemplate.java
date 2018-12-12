package com.massestech.common.mybatis.provider.base;

import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.utils.ReflectUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;

/**
 * 删除的基础模板
 * @param <T>
 */
public abstract class AbstractDeleteTemplate<T extends EntityAware> extends AbstractTemplate<T> {

    /**
     * 根据条件去进行删除
     * @param obj
     * @return
     */
    public String delete(T obj) {
        String where = whereSql(obj);
        // 不允许全表删除...
        if ("".equals(where)) {
            return null;
        } else {
            StringBuilder sqlStr = new StringBuilder();
            SQL sql = new SQL();
            sql.UPDATE(ReflectUtils.tableName(obj.getClass()));
            sql.SET(deleteSetSql());
            sql.WHERE(where);
            sqlStr.append(sql.toString());
            return sqlStr.toString();
        }
    }

    /**
     * 根据model删除的set条件
     * @return
     */
    protected abstract String deleteSetSql();

    /**
     * 根据id去删除
     * @param context
     * @return
     */
    public String deleteById(ProviderContext context) {
        SQL sql = new SQL();
        sql.UPDATE(tableNameByMapperClass(context.getMapperType()));
        sql.SET(deleteByIdSetSql());
        sql.WHERE(deleteByIdWhereSql());
        return sql.toString();
    }

    /**
     * 根据id删除的set条件
     * @return
     */
    protected abstract String deleteByIdSetSql();

    /**根据id删除的where条件sql,默认是id=#{id}*/
    protected String deleteByIdWhereSql(){
        return "id =#{id}";
    }


    /**
     * 批量删除
     * @param context
     * @param ids
     * @return
     */
    public <K> String deleteBatch(ProviderContext context, @Param("ids") final K[] ids) {
        SQL sql = new SQL();
        sql.UPDATE(tableNameByMapperClass(context.getMapperType()));
        sql.SET(deleteByIdSetSql());
        sql.WHERE(deleteBatchWhereSql(ids));
        return sql.toString();
    }

    /**
     * 批量删除的条件sql
     * @return
     */
    protected abstract <K> String deleteBatchWhereSql(K[] ids);


    /**
     * 提供的根据id来删除的
     * @param ids
     * @param sb
     */
    protected void setIds(Object[] ids, StringBuilder sb) {
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("#{ids[");
            sb.append(i);
            sb.append("]}");
        }
    }

}
