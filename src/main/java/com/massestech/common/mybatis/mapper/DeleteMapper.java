package com.massestech.common.mybatis.mapper;

import com.massestech.common.mybatis.provider.DeleteTemplate;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础的删除mapper
 * @param <T>
 */
@NoRepositoryBean
public interface DeleteMapper<T> extends MapperAware<T> {

    /**
     * 根据id去进行删除
     * @param id
     * @param <K>
     * @return
     */
    @DeleteProvider(
            type = DeleteTemplate.class,
            method = "deleteById"
    )
    <K> int deleteById(K id);

    /**
     * 批量删除
     * @param ids
     * @param <K>
     * @return
     */
    @DeleteProvider(
            type = DeleteTemplate.class,
            method = "deleteBatch"
    )
    <K> int deleteBatchIds(@Param("ids") K[] ids);

    /**
     * 根据条件查询
     * @param entity
     * @return
     */
    @DeleteProvider(
            type = DeleteTemplate.class,
            method = "delete"
    )
    int delete(T entity);

}
