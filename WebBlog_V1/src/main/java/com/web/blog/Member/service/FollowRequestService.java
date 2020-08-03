package com.web.blog.Member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.blog.Member.entity.FollowRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowRequestService {
    private final ObjectMapper objectMapper;
    private Map<String, FollowRequest> followRequests;

    @PostConstruct
    private void init() {
        followRequests = new LinkedHashMap<>();
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
