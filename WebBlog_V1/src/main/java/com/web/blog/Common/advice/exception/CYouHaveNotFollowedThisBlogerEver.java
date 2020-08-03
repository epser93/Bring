package com.web.blog.Common.advice.exception;

public class CYouHaveNotFollowedThisBlogerEver extends RuntimeException {
    public CYouHaveNotFollowedThisBlogerEver(String msg, Throwable t) {
        super(msg, t);
    }

    public CYouHaveNotFollowedThisBlogerEver(String msg) {
        super(msg);
    }

    public CYouHaveNotFollowedThisBlogerEver() {
        super();
    }
}
