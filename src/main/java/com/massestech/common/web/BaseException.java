package com.massestech.common.web;

import lombok.Data;

/**
 * 基础的错误类
 */
@Data
public class BaseException extends RuntimeException{

    private int code = 500;

    private String msg;

    public BaseException() {}

    public BaseException(String message){
        super(message);
    }

}
