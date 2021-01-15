package com.web.blog.Common.advice.exception;

public class CFileUploadException extends RuntimeException {
    public CFileUploadException(String msg, Throwable t) {
        super(msg, t);
    }

    public CFileUploadException(String msg) {
        super(msg);
    }

    public CFileUploadException() {
        super();
    }
}
