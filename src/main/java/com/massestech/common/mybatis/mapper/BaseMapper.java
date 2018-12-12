package com.massestech.common.mybatis.mapper;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础mapper
 * @param <T>
 */
@NoRepositoryBean
public interface BaseMapper<T> extends InsertMapper<T>,
        SelectMapper<T>, DeleteMapper<T>, UpdateMapper<T> {

}
