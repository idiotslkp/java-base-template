package com.massestech.javabasetemplate.domain;

import com.massestech.common.domain.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "enumerate")
public class EnumerateEntity extends BaseEntity {

    @Column(name = "key", columnDefinition = "key")
    private Integer key;

    @Column(name = "key_str", columnDefinition = "value")
    private String keyStr;

}
