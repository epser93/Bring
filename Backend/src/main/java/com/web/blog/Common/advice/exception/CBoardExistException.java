package com.web.blog.Common.advice.exception;

public class CBoardExistException extends RuntimeException {
    public CBoardExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public CBoardExistException(String msg) {
        super(msg);
    }

    public CBoardExistException() {
        super();
    }
}
