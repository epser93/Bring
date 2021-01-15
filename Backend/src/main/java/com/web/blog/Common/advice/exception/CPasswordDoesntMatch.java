package com.web.blog.Common.advice.exception;

public class CPasswordDoesntMatch extends RuntimeException{
    public CPasswordDoesntMatch(String msg, Throwable t) {
        super(msg, t);
    }

    public CPasswordDoesntMatch(String msg) {
        super(msg);
    }

    public CPasswordDoesntMatch() {
        super();
    }
}
