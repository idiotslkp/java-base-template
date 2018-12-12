package com.massestech.common.mybatis.handler;


import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用于将like的查询变成模糊查询<br/>
 * ex: name like #{name, typeHandler=likeTypeHandler}
 */
public class LikeTypeHandler extends BaseTypeHandler {
    static final String like = "%";
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException
    {
        ps.setString(i, like+parameter+like);
    }

    @Override
    public Void getNullableResult(ResultSet rs, String columnName) throws SQLException
    {
        throw new SQLException();
    }

    @Override
    public Void getNullableResult(ResultSet rs, int columnIndex) throws SQLException
    {
        throw new SQLException();
    }

    @Override
    public Void getNullableResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        throw new SQLException();
    }
}

