package com.yuyu.exception;

/**
 * 自定义异常类，用于业务层中出现的异常
 */
public class BussinessException extends RuntimeException{
    private Integer code;

    public BussinessException( Integer code,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BussinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
