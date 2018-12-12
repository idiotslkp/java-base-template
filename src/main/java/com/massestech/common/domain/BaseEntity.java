package com.massestech.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.massestech.common.mybatis.sqlfilter.SqlFilterAdapter;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 基础的父类
 */
@Data
@MappedSuperclass
public class BaseEntity implements EntityAware {
//        , SqlFilterAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreationTimestamp
    @Column(
            name = "created_time"
    )
    protected Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @UpdateTimestamp
    @Column(
            name = "last_update_time"
    )
    protected Date lastUpdateTime;

    protected boolean deleted = false;

    // 自定义查询
    @JsonIgnore
    @Transient
    private SqlFilterAdapter sqlFilterAdapter;
    // 正序
    @JsonIgnore
    @Transient
    private String orderByDesc;
    // 倒序
    @JsonIgnore
    @Transient
    private String orderBy;

}
