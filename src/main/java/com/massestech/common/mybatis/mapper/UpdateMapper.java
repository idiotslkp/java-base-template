package com.massestech.common.mybatis.mapper;

import com.massestech.common.mybatis.provider.UpdateTemplate;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础的修改mapper
 * @param <T>
 */
@NoRepositoryBean
public interface UpdateMapper<T> extends MapperAware<T> {

    /**
     * 根据id全量修改
     * @param entity
     * @return
     */
    @UpdateProvider(
            type = UpdateTemplate.class,
            method = "update"
    )
    int update(T entity);

    /**
     * 根据id,只修改有值的字段
     * @param entity
     * @return
     */
    @UpdateProvider(
            type = UpdateTemplate.class,
            method = "updateSelective"
    )
    int updateSelective(T entity);

    /**
     * 根据sqlFilter的条件进行更新,只会修改有值的字段.
     * @param entity
     * @return
     */
    @UpdateProvider(
            type = UpdateTemplate.class,
            method = "updateSelectiveSqlFilter"
    )
    int updateSelectiveSqlFilter(T entity);

}
