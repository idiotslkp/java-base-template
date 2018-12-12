package com.massestech.javabasetemplate.repository;

import com.massestech.common.mybatis.mapper.BaseMapper;
import com.massestech.common.mybatis.provider.SelectTemplate;
import com.massestech.javabasetemplate.controller.domain.UserView;
import com.massestech.javabasetemplate.domain.UserEntity;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<UserEntity> {

    @SelectProvider(
            type = SelectTemplate.class,
            method = "find"
    )
    UserView findUserViewList(UserEntity userEntity);

    /**
     * 查询,会分页.
     * @param userEntity
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "findList"
    )
    List<UserView> pageQuery(UserEntity userEntity);

    /**
     * 求和
     * @return
     */
    @SelectProvider(
            type = SelectTemplate.class,
            method = "find"
    )
    int sum(UserEntity userEntity);

}
