package com.massestech.common.mybatis.sqlfilter;

/**
 * sql辅助查询
 */
public interface SqlFilter {

    /**
     * 得到拼接的sql
     */
    String sql();

    /**
     * 字段名.用于在拼接sql的时候避免重复
     */
    String getProperty();

}
