package com.web.blog.Common.advice.exception;

public class CSelectedAnswerException extends RuntimeException {
    public CSelectedAnswerException(String msg, Throwable t) {
        super(msg, t);
    }

    public CSelectedAnswerException(String msg) {
        super(msg);
    }

    public CSelectedAnswerException() {
        super();
    }
}
