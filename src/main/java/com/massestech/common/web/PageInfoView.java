package com.massestech.common.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页查询")
public class PageInfoView<T> {
    @ApiModelProperty("查询页数")
    private int pageNum;
    @ApiModelProperty("每页记录数")
    private int pageSize;
    /**
     * 查询条件
     **/
    private T param;

}
