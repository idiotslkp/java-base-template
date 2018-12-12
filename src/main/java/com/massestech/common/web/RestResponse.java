package com.massestech.common.web;

import lombok.Data;

/**
 * 响应对象
 */
@Data
public class RestResponse<T> {
    /**
     * 响应码
     */
    private int code;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 响应对象
     */
    private T data;
}
