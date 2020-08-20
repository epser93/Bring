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
@RedisHash("today_ip")
public class IpAddrForTodayCnt implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Indexed
    private String ip;
    @Indexed
    private String nickname;

    @TimeToLive
    Long timeout;

    @Builder
    public IpAddrForTodayCnt(String ip, String nickname) {
        this.ip = ip;
        this.nickname = nickname;
    }
}
