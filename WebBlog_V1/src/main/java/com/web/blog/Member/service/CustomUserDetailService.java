package com.web.blog.Member.service;

import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository repository;

    public UserDetails loadUserByUsername(String userPk) {
        return repository.findById(Long.valueOf(userPk)).orElseThrow(CUserNotFoundException::new);
    }
}
