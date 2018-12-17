package com.massestech.common.mybatis.provider.base;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.domain.EntityAware;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Iterator;
import java.util.Map;

/**
 * 基础的修改模板
 * @param <T>
 */
public class AbstractUpdateTemplate<T extends EntityAware> extends AbstractTemplate<T> {

    /**
     * 全字段更新
     * @param obj
     * @return
     */
    public String update(T obj) {
        return this.getUpdateSql(obj, true);
    }

    /**
     * 只更新有值的字段
     * @param obj
     * @return
     */
    public String updateSelective(T obj) {
        return this.getUpdateSql(obj, false);
    }

    protected String getUpdateSql(T obj, boolean isSetNotNull) {
        SQL sql = new SQL();
        sql.UPDATE(SqlUtil.tableName(obj.getClass()));
        String returnUpdateSet = updateSetSql(obj, isSetNotNull);
        sql.SET(returnUpdateSet);
        String updateWhereSql = this.whereByIdSql();
        if (StringUtils.isEmpty(updateWhereSql)) {
            throw new IllegalArgumentException("必须要有where条件,不允许进行全表更新.");
        }
        sql.WHERE(updateWhereSql);
        return sql.toString();
    }
    /**默认是id=#{id}*/
    protected String whereByIdSql() {
        return "id=#{id}";
    }

    /**
     * 根据条件进行更新,不会更新实体里面的id,另外,更新条件只会是sqlFilter
     * @param obj
     * @return
     */
    public String updateSelectiveSqlFilter(T obj) {
        SQL sql = new SQL();
        sql.UPDATE(SqlUtil.tableName(obj.getClass()));
        sql.SET(this.updateSetSql(obj, false));
        String where = this.updateWhereSql(obj);
        if (!"".equals(where)) {
            sql.WHERE(where);
            return sql.toString();
        } else {
            // 不允许全表删除.
            return null;
        }
    }

    /**
     * 获取update的setSql
     * @param obj 需要拼接的obj
     * @param isSetNotNull 空字段是否要拼接上
     * @return
     */
    protected String updateSetSql(T obj, boolean isSetNotNull) {
        StringBuilder updateSetSb = new StringBuilder();
        preProcessUpdateSetSql(obj, updateSetSb, isSetNotNull);
        processUpdateSetSql(obj, updateSetSb, isSetNotNull);
        postProcessUpdateSetSql(obj, updateSetSb, isSetNotNull);
        return updateSetSb.toString();
    }
    /**默认的空实现,留给子类去进行增强*/
    protected void preProcessUpdateSetSql(T obj, StringBuilder updateSetSb, boolean isSetNotNull) {
    }
    /**处理sql语句*/
    protected void processUpdateSetSql(T obj, StringBuilder updateSetSb, boolean isSetNotNull) {
        Class<? extends EntityAware> entityClass = obj.getClass();
        Map<String, String> columnAndFieldsMap = SqlUtil.getColumnAndFieldsMap(entityClass);
        for (String column : columnAndFieldsMap.keySet()) {
            // 设置的不为空才会加进去,并且属性值也刚好不为空
            // 首先判断是否是全量更新,如果是,那么直接全量更新
            if (isSetNotNull) {
                if (updateSetSb.length() > 0) {
                    updateSetSb.append(",");
                }
                updateSetSb.append(column).append("=").append("#{").append(columnAndFieldsMap.get(column)).append("}");
            } else if (!ReflectUtils.isNull(obj, columnAndFieldsMap.get(column))) {
                // 不是全量更新,需要判断是否有值,只有有值的,才需要加入到set条件之中
                if (updateSetSb.length() > 0) {
                    updateSetSb.append(",");
                }
                updateSetSb.append(column).append("=").append("#{").append(columnAndFieldsMap.get(column)).append("}");
            }
        }
    }
    /**默认的空实现,留给子类去进行增强*/
    protected void postProcessUpdateSetSql(T obj, StringBuilder updateSetSb, boolean isSetNotNull) {
    }

    public String updateWhereSql(T obj) {
        StringBuilder sb = new StringBuilder();
        if (obj instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) obj;

            if (baseEntity.getSqlFilterAdapter() != null &&
                    null != baseEntity.getSqlFilterAdapter().getWhereSqlFilterList()
                    && baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().size() > 0) {
                Iterator<SqlFilter> sqlFilterIterator = baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().iterator();
                while(sqlFilterIterator.hasNext()) {
                    SqlFilter filter = sqlFilterIterator.next();

                    if (null != filter && StringUtils.isNotEmpty(filter.sql())) {
                        if (StringUtils.isNotEmpty(sb.toString())) {
                            sb.append(" and ");
                        }
                        sb.append(filter.sql());
                    }
                }

                if (StringUtils.isNotEmpty(sb) && !SqlUtil.isSpiteParams(sb.toString().toLowerCase())) {
                    logger.error("Malice SQL keyword : {}", sb.toString());
                }
            }
        }

        return sb.toString();
    }

}
