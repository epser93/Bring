package com.web.blog.Member.repository;

import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.OnlyMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUid(String email);

    Member findAllByUid(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<OnlyMemberMapping> findByMsrl(long msrl);

    Optional<OnlyMemberMapping> findAllByNickname(String nickname);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set ip_addr = :ip_addr where uid = :uid", nativeQuery = true)
    void updateIpAddr(@Param("ip_addr") String ip_addr);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set todayCnt = todayCnt + 1 where uid = :uid", nativeQuery = true)
    void updateTodayCnt(@Param("uid") String uid);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set totalCnt = totalCnt + todayCnt where uid = :uid", nativeQuery = true)
    void updateTotalCnt(@Param("uid") String uid);

    @Query(value = "select nickname, score from userinfo order by score desc", nativeQuery = true)
    List<Map<String, Integer>> rank();

    @Modifying
    @Transactional
    @Query(value = "update userinfo set likedpost = :likedpost where msrl = :msrl", nativeQuery = true)
    int updateLikeCnt(@Param("likedpost") int likedpost, @Param("msrl") long msrl);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set score = score + 10 where msrl = :msrl", nativeQuery = true)
    int updateScoreIfSelected(@Param("msrl") long msrl);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set score = score + 1 where msrl = :msrl", nativeQuery = true)
    int updateScoreIfLiked(@Param("msrl") long msrl);

    @Modifying
    @Transactional
    @Query(value = "update userinfo set score = score - 1 where msrl = :msrl", nativeQuery = true)
    int updateScoreIfUnliked(@Param("msrl") long msrl);

    //팔로우 요청 후 상대방 수락시
    @Modifying
    @Transactional
    @Query(value = "update userinfo set following_cnt = following_cnt + 1 where msrl = :msrl", nativeQuery = true)
    int updateFollowing(@Param("msrl") long msrl);

    //팔로우 요청받고 내가 수락시
    @Modifying
    @Transactional
    @Query(value = "update userinfo set followers_cnt = followers_cnt + 1 where msrl = :msrl", nativeQuery = true)
    int updateFollower(@Param("msrl") long msrl);

    //팔로우 삭제시 삭제 한 유저쪽 변화
    @Modifying
    @Transactional
    @Query(value = "update userinfo set following_cnt = following_cnt - 1 where msrl = :msrl", nativeQuery = true)
    int updateUnfollowing(@Param("msrl") long msrl);

    //팔로우 삭제시 삭제 당한 유저쪽 변화
    @Modifying
    @Transactional
    @Query(value = "update userinfo set followers_cnt = followers_cnt - 1 where msrl = :msrl", nativeQuery = true)
    int updateUnfollower(@Param("msrl") long msrl);
}
