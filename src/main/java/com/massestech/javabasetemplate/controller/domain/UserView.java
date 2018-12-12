package com.massestech.javabasetemplate.controller.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户视图")
@Data
public class UserView {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("年龄")
    private Integer age;
    @ApiModelProperty("性别")
    private Integer sexStatus;

}
