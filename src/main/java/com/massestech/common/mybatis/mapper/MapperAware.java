package com.massestech.common.mybatis.mapper;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * 标识这个是一个mapper
 * @param <T>
 */
@NoRepositoryBean
public interface MapperAware<T> {
}
