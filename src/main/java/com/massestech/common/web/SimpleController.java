package com.massestech.common.web;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ValidationException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Created by lyb on 2016/10/8.
 */
public abstract class SimpleController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
//    private int code = -1;
    private String message ="服务器异常";

    protected RestResponse success(){
        RestResponse restResponse = new RestResponse();
        restResponse.setCode(0);
        restResponse.setMsg("ok");

        return restResponse;
    }

    protected RestResponse success(Object data){
        RestResponse restResponse = new RestResponse();
        restResponse.setCode(0);
        restResponse.setMsg("ok");
        restResponse.setData(data);
        return restResponse;
    }

    protected ResponseEntity fail(String msg) {
        return ResponseEntity.ok().body(this.fail(500, msg));
    }

    protected RestResponse fail(int code, String msg) {
        RestResponse restResponse = new RestResponse();
        restResponse.setCode(code);
        restResponse.setMsg(msg);
        return restResponse;
    }

    protected Pageable getPageable(Integer pageNum, Integer pageSize){
        return this.pageable(pageNum, pageSize, null);
    }

    protected Pageable pageable(Integer pageNum, Integer pageSize, Sort sort){
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return new PageRequest(pageNum - 1, pageSize, sort);
    }

    /**
     * 统一处理异常
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public RestResponse exceptionHandler(Exception e) {
        int code = 500;
        String errMsg = message;
        // 校验异常
        if (e instanceof MethodArgumentNotValidException) {
            // 获取错误信息字段,并拼接.
            FieldError fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
            errMsg = fieldError.getField() + fieldError.getDefaultMessage();
        } else if (e instanceof BadSqlGrammarException) {
            errMsg = "sql报错.";
        }else if(e instanceof BaseException) {
            logger.error("异常：{}", e.getMessage());
            errMsg = e.getMessage();
            code = ((BaseException) e).getCode();
        }else if(e instanceof Exception) {
            logger.error("异常：{}, {}", e.getMessage(), e.getStackTrace());
            errMsg = "系统繁忙";
        }
        // debug情况下才会去打印异常.
        if (logger.isDebugEnabled()) {
            e.printStackTrace();
        }
    // Spring MVC异常,后续如果遇到了,那么就完善对应的异常信息.
        //        if (ex instanceof org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException) {
//            return handleNoSuchRequestHandlingMethod((org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException) ex,
//                    request, response, handler);
//        }
//        else if (ex instanceof HttpRequestMethodNotSupportedException) {
//            return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, request,
//                    response, handler);
//        }
//        else if (ex instanceof HttpMediaTypeNotSupportedException) {
//            return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, request, response,
//                    handler);
//        }
//        else if (ex instanceof HttpMediaTypeNotAcceptableException) {
//            return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, request, response,
//                    handler);
//        }
//        else if (ex instanceof MissingPathVariableException) {
//            return handleMissingPathVariable((MissingPathVariableException) ex, request,
//                    response, handler);
//        }
//        else if (ex instanceof MissingServletRequestParameterException) {
//            return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, request,
//                    response, handler);
//        }
//        else if (ex instanceof ServletRequestBindingException) {
//            return handleServletRequestBindingException((ServletRequestBindingException) ex, request, response,
//                    handler);
//        }
//        else if (ex instanceof ConversionNotSupportedException) {
//            return handleConversionNotSupported((ConversionNotSupportedException) ex, request, response, handler);
//        }
//        else if (ex instanceof TypeMismatchException) {
//            return handleTypeMismatch((TypeMismatchException) ex, request, response, handler);
//        }
//        else if (ex instanceof HttpMessageNotReadableException) {
//            return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, request, response, handler);
//        }
//        else if (ex instanceof HttpMessageNotWritableException) {
//            return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, request, response, handler);
//        }
//        else if (ex instanceof MethodArgumentNotValidException) {
//            return handleMethodArgumentNotValidException((MethodArgumentNotValidException) ex, request, response,
//                    handler);
//        }
//        else if (ex instanceof MissingServletRequestPartException) {
//            return handleMissingServletRequestPartException((MissingServletRequestPartException) ex, request,
//                    response, handler);
//        }
//        else if (ex instanceof BindException) {
//            return handleBindException((BindException) ex, request, response, handler);
//        }
//        else if (ex instanceof NoHandlerFoundException) { // 这个应该就是用来替代NoSuchRequestHandlingMethodException的
//            return handleNoHandlerFoundException((NoHandlerFoundException) ex, request, response, handler);
//        }
//        else if (ex instanceof AsyncRequestTimeoutException) {
//            return handleAsyncRequestTimeoutException(
//                    (AsyncRequestTimeoutException) ex, request, response, handler);
//        }

        return fail(code, errMsg);
    }
}
