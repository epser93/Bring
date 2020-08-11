package com.web.blog.Board.repository;

import com.web.blog.Board.entity.Board;
import com.web.blog.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByName(String name);

    List<Board> findByMember(Member member);

    Board findByNameAndMember(String name, Member member);

    @Modifying
    @Transactional
    @Query(value = "UPDATE board SET post_cnt = post_cnt + 1 where board_id = :board_id", nativeQuery = true)
    int updatePostCnt(@Param("board_id") long board_id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE board SET post_cnt = post_cnt - 1 where board_id = :board_id", nativeQuery = true)
    int updatePostCntMinus(@Param("board_id") long board_id);
}
