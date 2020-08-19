package com.web.blog.Member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@ToString
@RedisHash("view_ip")
public class IpAddrForViewCnt implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Indexed
    private String ip;
    @Indexed
    private Long postId;
    @Indexed
    private Long qpostId;

    @TimeToLive
    Long timeout;

    @Builder
    public IpAddrForViewCnt(String ip, Long postId, Long qpostId, Long timeout) {
        this.ip = ip;
        this.postId = postId;
        this.qpostId = qpostId;
        this.timeout = timeout;
    }
}
