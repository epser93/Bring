package com.web.blog.Member.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequest {
    public enum ResponseType {
        REQUEST, ACCEPT, DECLINE
    }

    private ResponseType type;
    private String sender;
    private String receiver;
    private String message;
}
