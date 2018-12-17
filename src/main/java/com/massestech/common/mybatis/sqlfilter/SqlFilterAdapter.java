package com.massestech.common.mybatis.sqlfilter;

import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * 过滤器适配器
 */
@Data
public class SqlFilterAdapter {

    /** 查询的字段 */
    private List<SqlFilter> columnSqlFilterList;
    /** where条件 */
    private List<SqlFilter> whereSqlFilterList;
    /** join条件 */
    private SqlFilter joinSqlFilter;
    /** 用于存放sqlFilter的查询参数,在查询的时候可以使用预编译语句去进行查询 */
    private Map<String, Object> sqlFilterParamMap;

}
