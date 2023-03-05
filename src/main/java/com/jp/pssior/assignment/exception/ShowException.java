package com.jp.pssior.assignment.exception;

import com.jp.pssior.assignment.constant.ErrorCode;

import java.text.MessageFormat;

public class ShowException extends Exception{

    private ErrorCode errorCode;

    public ShowException(ErrorCode errorCode, String... args){
        super(MessageFormat.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
