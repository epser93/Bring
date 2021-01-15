package com.web.blog.Common.advice.exception;

public class CAnsweredQuestionException extends RuntimeException {
    public CAnsweredQuestionException(String msg, Throwable t) {
        super(msg, t);
    }

    public CAnsweredQuestionException(String msg) {
        super(msg);
    }

    public CAnsweredQuestionException() {
        super();
    }
}
