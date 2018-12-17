package com.massestech.common.mybatis.sqlfilter.where;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.ParamMapSqlFilter;
import com.massestech.common.mybatis.sqlfilter.SqlFilter;
import com.massestech.common.mybatis.utils.ReflectUtils;
import com.massestech.common.mybatis.utils.SqlUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * select property from table where column=#{column}
 */
public class TableSqlFilter extends ParamMapSqlFilter {

    public TableSqlFilter(String property, Object value, Map<String, Object> sqlFilterParamMap) {
        super(property, value, sqlFilterParamMap);
        // 进行参数的合并
        BaseEntity baseEntity = (BaseEntity) value;
        if (null != baseEntity.getSqlFilterAdapter()) {
            // 将参数合并到要查询的model里面的map里面去。
            Map<String, Object> entityParamMap = baseEntity.getSqlFilterAdapter().getSqlFilterParamMap();
            sqlFilterParamMap.putAll(entityParamMap);
        }
    }

    @Override
    protected void appendSql() {
        // 校验，这个应该不会有问题。
        if (value instanceof BaseEntity) {
            // select property from table where 条件 and dr = 0
            BaseEntity baseEntity = (BaseEntity) value;
            String tableName = SqlUtil.tableName(baseEntity.getClass());
            sb.append(SqlCons.SELECT)
                    .append(this.getColumnName())
            .append(SqlCons.FROM)
                    .append(tableName);

            sb.append(SqlCons.WHERE);
            // 设置查询条件
            processWhereSql(baseEntity, sb);

        } else {
            throw new RuntimeException("InTableSqlFilter传递的参数value必须是：" + BaseEntity.class + " 子类");
        }
    }

    /**
     * 处理where条件，因为这个是子查询的，所以我们需要将他的查询条件放在map之中
     * @param baseEntity
     * @param whereSb
     */
    public void processWhereSql(BaseEntity baseEntity, StringBuilder whereSb) {
        // 该set用于判断字段是否已经存在，避免重复进行条件查询
        Set<String> columnSet = new HashSet<>();
        // 拼接sqlFilter语句
        if (baseEntity.getSqlFilterAdapter() != null &&
                null != baseEntity.getSqlFilterAdapter().getWhereSqlFilterList()
                && baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().size() > 0) {
            Iterator<SqlFilter> filterIteratort = baseEntity.getSqlFilterAdapter().getWhereSqlFilterList().iterator();

            while(filterIteratort.hasNext()) {
                SqlFilter filter = filterIteratort.next();
                if (filter != null && !StringUtils.isEmpty(filter.sql())) {
                    if (!"".equals(whereSb.toString())) {
                        whereSb.append(SqlCons.AND);
                    }
                    whereSb.append(filter.sql());
                    columnSet.add(filter.getProperty());
                }
            }
        }

        // 普通查询语句
        Map<String, String> columnAndFieldsMap = SqlUtil.getColumnAndFieldsMap(baseEntity.getClass());
        for (String column : columnAndFieldsMap.keySet()) {
            String model_attribute = columnAndFieldsMap.get(column);
            if (!ReflectUtils.isNull(baseEntity, model_attribute)) {
                // 判断,如果sqlFilter里面对应的字段已经有了,那么entity里面的字段,就不拼接进去了.
                if (!columnSet.contains(model_attribute)) {
                    columnSet.add(model_attribute);
                    // whereSb已经有值的情况下，那么需要加一个and，否则就不需要加。
                    if (!"".equals(whereSb.toString())) {
                        whereSb.append(SqlCons.AND);
                    }

                    String valueKey = setValueAndGetKey(ReflectUtils.getFieldValue(baseEntity, model_attribute));

                    whereSb.append(column).append("=").append(valueKey);
                }
            }
        }
    }

}
