package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Board;
import com.web.blog.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByName(String name);

    List<Board> findByMember(Member member);

    Board findByNameAndMember(String name, Member member);
}
