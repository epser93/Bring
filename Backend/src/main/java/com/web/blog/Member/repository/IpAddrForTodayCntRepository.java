package com.web.blog.Member.repository;

import com.web.blog.Member.entity.IpAddrForTodayCnt;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IpAddrForTodayCntRepository extends CrudRepository<IpAddrForTodayCnt, Long> {
    Optional<IpAddrForTodayCnt> findByIpAndNickname(String ip, String nickname);
}
