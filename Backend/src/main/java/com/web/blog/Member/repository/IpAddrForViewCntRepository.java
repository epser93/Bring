package com.web.blog.Member.repository;

import com.web.blog.Member.entity.IpAddrForViewCnt;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IpAddrForViewCntRepository extends CrudRepository<IpAddrForViewCnt, Long> {
    Optional<IpAddrForViewCnt> findByIpAndPostId(String ip, long postId);

    Optional<IpAddrForViewCnt> findByIpAndQpostId(String ip, long qpostId);
}
