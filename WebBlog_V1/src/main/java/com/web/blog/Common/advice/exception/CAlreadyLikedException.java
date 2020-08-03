package com.web.blog.Common.advice.exception;

public class CAlreadyLikedException extends RuntimeException {
    public CAlreadyLikedException(String msg, Throwable t) {
        super(msg, t);
    }

    public CAlreadyLikedException(String msg) {
        super(msg);
    }

    public CAlreadyLikedException() {
        super();
    }
}
