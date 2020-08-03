package com.web.blog.Common.advice.exception;

public class COwnerCannotLike extends RuntimeException {
    public COwnerCannotLike(String msg, Throwable t) {
        super(msg, t);
    }

    public COwnerCannotLike(String msg) {
        super(msg);
    }

    public COwnerCannotLike() {
        super();
    }
}
