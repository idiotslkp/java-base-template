package com.massestech.javabasetemplate.domain;

import com.massestech.common.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

@Data
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Column(name = "user_name", columnDefinition = "用户名")
    private String userName;
    @Column(name = "age", columnDefinition = "年龄")
    private Integer age;
    @Column(name = "sex_status", columnDefinition = "性别,0.男,1.女")
    private Integer sexStatus;

    private EnumerateEntity enumerate;
    /** 性别 */
    private String sexStr;
    /** 书籍 */
    private List<BookEntity> bookEntityList;

}
