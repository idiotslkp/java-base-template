package com.massestech.common.mybatis.mapper;

import com.massestech.common.mybatis.provider.InsertTemplate;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 基础的新增mapper
 * @param <T>
 */
@NoRepositoryBean
public interface InsertMapper<T> extends MapperAware<T> {

    /**
     * 新增单条数据
     * @param entity
     * @return
     */
    @InsertProvider(
            type = InsertTemplate.class,
            method = "insert"
    )
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    long insert(T entity);

    /**
     * 新增多条数据
     * @param entityList
     * @return
     */
    @InsertProvider(
            type = InsertTemplate.class,
            method = "insertBatch"
    )
    int insertBatch(@Param("objList") List<T> entityList);

}
