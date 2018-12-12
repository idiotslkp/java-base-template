package com.massestech.common.mybatis.sqlfilter;

import com.massestech.common.domain.BaseEntity;
import com.massestech.common.mybatis.cons.SqlCons;
import com.massestech.common.mybatis.sqlfilter.where.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.massestech.common.mybatis.sqlfilter.SqlFilterBuilder.isNotEmpty;

/**
 * 查询条件
 */
public class WhereSqlFilterBuilder {

    private BaseEntity entity;

    /** where条件 */
    private List<SqlFilter> whereSqlFilterList;
    /** 用于存放参数 */
    private Map<String, Object> sqlFilterParamMap;
    /** 随机数，用于存放参数是使用 */
    private long randomLong;


    public WhereSqlFilterBuilder(BaseEntity entity) {
        this.entity = entity;
        whereSqlFilterList = new ArrayList<>();
        this.entity.getSqlFilterAdapter().setWhereSqlFilterList(whereSqlFilterList);
        sqlFilterParamMap = new HashMap<>();
        this.entity.getSqlFilterAdapter().setSqlFilterParamMap(sqlFilterParamMap);
        randomLong = new Random().nextLong();
    }

    /**
     * property = value
     */
    public WhereSqlFilterBuilder eq(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new EqualSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property <> value
     */
    public WhereSqlFilterBuilder ne(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new NotEqualSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property > value
     */
    public WhereSqlFilterBuilder gt(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new GtSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property < value
     */
    public WhereSqlFilterBuilder lt(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new LtSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property >= value
     */
    public WhereSqlFilterBuilder ge(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new GeSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property <= value
     */
    public WhereSqlFilterBuilder le(String property, Object value) {
        if (isNotEmpty(value)) {
            String valueKey = getValueKey(value);

            whereSqlFilterList.add(new LeSqlFilter(property, valueKey));
        }
        return this;
    }

    /**
     * property like #{value, typeHandler=likeTypeHandler}
     *
     * 注意,需要注册likeTypeHandler
     */
    public WhereSqlFilterBuilder like(String property, Object value) {
        if (isNotEmpty(value)) {
//            String key = "param_" + whereSqlFilterList.size();
            String key = "param_" + sqlFilterParamMap.size();
            sqlFilterParamMap.put(key, value);
            String valueKey = "#{sqlFilterAdapter.sqlFilterParamMap." + key + ", typeHandler=likeTypeHandler}";

            LikeSqlFilter sqlFilter = new LikeSqlFilter(property, valueKey);
            sqlFilter.setFieldName(value.toString());
            whereSqlFilterList.add(sqlFilter);
        }
        return this;
    }

    /**
     * property in (value)
     * @param property sql字段名
     * @param value 字段的值，这里能接收'str,str,str'这种形式的字符串，也能接收list，或者object[]这种类型的数组
     */
    public WhereSqlFilterBuilder in(String property, Object value) {
        if (isNotEmpty(value)) {
            // 这里如果接收的是list,那么就自动转化.
//            String valueStr;
//            if (value instanceof List) {
//                valueStr = StringUtils.join(value, ",");
//            } else {
//                valueStr = value.toString();
//            }
//            String valueKey = getValueKey(valueStr);

            whereSqlFilterList.add(new InSqlFilter(property, value, sqlFilterParamMap));
        }
        return this;
    }

    /** or条件 */
    public WhereSqlFilterBuilder or(SqlFilter sqlFilter, SqlFilter sqlFilter1) {
        whereSqlFilterList.add(new OrSqlFilter(sqlFilter, sqlFilter1));
        return this;
    }

    /**
     * beginValue <= property <= endValue
     * @param property
     * @param beginValue
     * @param endValue
     * @return
     */
    public WhereSqlFilterBuilder between(String property, Object beginValue, Object endValue) {
        return this.gt(property, beginValue).lt(property, endValue);
    }

//    /**
//     * beginProperty <= value <= endProperty
//     * @param beginProperty
//     * @param endProperty
//     * @param value
//     * @return
//     */
//    public WhereSqlFilterBuilder between(String beginProperty, String endProperty, Object value) {
//        if (isNotEmpty(value)) {
//            whereSqlFilterList.add(new LtSqlFilter(beginProperty, value));
//            whereSqlFilterList.add(new GtSqlFilter(endProperty, value));
//        }
//        return this;
//    }

    /**
     * property in (sqlFilter[select column from table where column = #{column}])
     * @param property
     * @param childTableProperty
     * @param childEntity
     * @return
     */
    public WhereSqlFilterBuilder inTableSql(String property, String childTableProperty, BaseEntity childEntity) {
        // 对参数进行合并
        TableSqlFilter tableSqlFilter = new TableSqlFilter(childTableProperty, childEntity, sqlFilterParamMap);
        ChildTableSqlFilter childTableSqlFilter = new ChildTableSqlFilter(property, SqlCons.IN, tableSqlFilter);
        whereSqlFilterList.add(childTableSqlFilter);
        return this;
    }

    /**
     * property = (sqlFilter[select column from table where column = #{column}])
     * @param property
     * @param childTableProperty
     * @param childEntity
     * @return
     */
    public WhereSqlFilterBuilder eqTableSql(String property, String childTableProperty, BaseEntity childEntity) {
        // 对参数进行合并
        TableSqlFilter tableSqlFilter = new TableSqlFilter(childTableProperty, childEntity, sqlFilterParamMap);
        ChildTableSqlFilter childTableSqlFilter = new ChildTableSqlFilter(property, SqlCons.EQ, tableSqlFilter);
        whereSqlFilterList.add(childTableSqlFilter);
        return this;
    }

    /** 原始sql */
    public WhereSqlFilterBuilder original(String sql) {
        if (isNotEmpty(sql)) {
            whereSqlFilterList.add(new OriginalSqlFilter(sql));
        }
        return this;
    }

    /**
     * 存放参数,并且获取到key
     * @param value
     * @return
     */
    private String getValueKey(Object value) {
        String key = "param_" + randomLong + "_" + sqlFilterParamMap.size();
        String valueKey = "#{sqlFilterAdapter.sqlFilterParamMap." + key + "}";
        sqlFilterParamMap.put(key, value);
        return valueKey;
    }

    public <T> T getEntity() {
        return (T) entity;
    }

}
