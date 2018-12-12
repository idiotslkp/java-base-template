package com.massestech.common.mybatis.mapper;

import com.massestech.common.mybatis.provider.SelectTemplate;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 基础的查询mapper
 * @param <T>
 */
@NoRepositoryBean
public interface SelectMapper<T> extends MapperAware<T> {

    /**
     * 根据id获取单条数据
     * @param id
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "findById"
    )
    T findById(Long id);

    /**
     * 根据条件查询
     * @param entity
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "find"
    )
    T find(T entity);

    /**
     * 根据条件查询列表
     * @param entity
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "findList"
    )
    List<T> findList(T entity);

    /**
     * 查询全部
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "findAll"
    )
    List<T> findAll();

    /**
     * 查询全部条数
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "count"
    )
    int count();

    /**
     * 根据条件查询条数
     * @param entity
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "countCondition"
    )
    int countCondition(T entity);

}
