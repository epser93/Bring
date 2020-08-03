package com.web.blog.Common.advice.exception;

public class CNicknameExistException extends RuntimeException {
    public CNicknameExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CNicknameExistException(String msg) {
        super(msg);
    }

    public CNicknameExistException() {
        super();
    }

}
