package com.web.blog.Common.advice.exception;

public class CSharedPostException extends RuntimeException {
    public CSharedPostException(String msg, Throwable t) {
        super(msg, t);
    }

    public CSharedPostException(String msg) {
        super(msg);
    }

    public CSharedPostException() {
        super();
    }
}
