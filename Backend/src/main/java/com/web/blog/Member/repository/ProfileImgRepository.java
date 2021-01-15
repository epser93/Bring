package com.web.blog.Member.repository;

import com.web.blog.Member.entity.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
    @Override
    List<ProfileImg> findAll();

    Optional<ProfileImg> findByMsrl(long msrl);

    void deleteByMsrl(long msrl);
}
