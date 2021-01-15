package com.web.blog.Common.advice.exception;

public class CAlreadyFollowedException extends RuntimeException {
    public CAlreadyFollowedException(String msg, Throwable t) {
        super(msg, t);
    }

    public CAlreadyFollowedException(String msg) {
        super(msg);
    }

    public CAlreadyFollowedException() {
        super();
    }
}
