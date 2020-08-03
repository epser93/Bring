package com.web.blog.Common.advice.exception;

public class CAskedQuestionException extends RuntimeException {
    public CAskedQuestionException(String msg, Throwable t) {
        super(msg, t);
    }

    public CAskedQuestionException(String msg) {
        super(msg);
    }

    public CAskedQuestionException() {
        super();
    }
}
