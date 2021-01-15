package com.web.blog.Member.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private String title;
    private String content;
    private String sender;
    private String receiver;
}
