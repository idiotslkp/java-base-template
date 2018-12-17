package com.massestech.javabasetemplate.domain;

import com.massestech.common.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "book")
public class BookEntity extends BaseEntity {

    @Column(name = "name", columnDefinition = "姓名")
    private String name;

    @Column(name = "price", columnDefinition = "价格")
    private String price;

    @Column(name = "user_id", columnDefinition = "用户id")
    private Long userId;

}
