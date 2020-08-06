package com.web.blog.Common.advice.exception;

public class CPasswordLengthException extends RuntimeException {
    public CPasswordLengthException(String msg, Throwable t) {
        super(msg, t);
    }

    public CPasswordLengthException(String msg) {
        super(msg);
    }

    public CPasswordLengthException() {
        super();
    }
}
