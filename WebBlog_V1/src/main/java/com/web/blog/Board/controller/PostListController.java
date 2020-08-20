package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Board.service.PostUploadsService;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.model.Paging;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.IpAddrForTodayCnt;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.IpAddrForTodayCntRepository;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.FollowService;
import com.web.blog.Member.service.ProfileImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"I. Post List"})
@RequiredArgsConstructor
@RestController
public class PostListController {

    private final PostRepository postRepository;
    private final BoardService boardService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final ProfileImgService profileImgService;
    private final PostUploadsService postUploadsService;
    private final PostUploadsRepository postUploadsRepository;
    private final FollowService followService;
    private final IpAddrForTodayCntRepository ipAddrForTodayCntRepository;

    //게시판 포스트 리스트
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 포스트 리스트", notes = "한 카테고리 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/{boardName}/post_list")
    public ListResult<ListResult> listPosts(@PathVariable String boardName, @PathVariable String nickname, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        Paging paging = new Paging(no);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardService.findBoard(boardName, member);
        List<OnlyPostMapping> list = boardService.CategoryPostList(board.getBoardId(), paging); //포스트 리스트
        result.add(responseService.getListResult(list)); //포스트 리스트 데이터 넘기기
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyPostMapping pm : list) {
                long postId = pm.getPostId();
                amIInTheList.add(false);
                if (postMemberRepository.findPostMemberByMember_MsrlAndPost_PostId(logined.get().getMsrl(), postId).isPresent()) {
                    amIInTheList.remove(cnt);
                    amIInTheList.add(true);
                }
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
                cnt++;
            }
        } else {
            for (OnlyPostMapping pm : list) {
                long postId = pm.getPostId();
                amIInTheList.add(false);
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }

            }
        }

        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //블로그 포스트 리스트
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "블로그 포스트 리스트", notes = "한 블로그 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/post_list")
    public ListResult<ListResult> listPosts(@PathVariable String nickname, @RequestParam(required = false, defaultValue = "1") long no, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<OnlyPostMapping> list = postRepository.findAllByMember_NicknameAndBoard_NameNotLikeOrderByPostIdDesc(nickname, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        result.add(responseService.getListResult(list));
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyPostMapping pm : list) {
                long postId = pm.getPostId();
                amIInTheList.add(false);
                if (postMemberRepository.findPostMemberByMember_MsrlAndPost_PostId(logined.get().getMsrl(), postId).isPresent()) {
                    amIInTheList.remove(cnt);
                    amIInTheList.add(true);
                }
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
                cnt++;
            }
            if(logined.get().getMsrl() != member.getMsrl()) {
                String ip = followService.getIpAddr(request);
                Optional<IpAddrForTodayCnt> ipAddr = ipAddrForTodayCntRepository.findByIpAndNickname(ip, member.getNickname());
                if (!ipAddr.isPresent()) {
                    IpAddrForTodayCnt checkCnt = IpAddrForTodayCnt.builder()
                            .ip(ip)
                            .nickname(member.getNickname())
                            .build();
                    checkCnt.setTimeout(86400L);
                    ipAddrForTodayCntRepository.save(checkCnt);
                    memberRepository.updateTodayCnt(member.getMsrl());
                    memberRepository.updateTotalCnt(member.getMsrl());
                    System.out.println("Im PostList");
                }
            }
        } else {
            for (OnlyPostMapping pm : list) {
                long postId = pm.getPostId();
                amIInTheList.add(false);
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
            String ip = followService.getIpAddr(request);
            Optional<IpAddrForTodayCnt> ipAddr = ipAddrForTodayCntRepository.findByIpAndNickname(ip, member.getNickname());
            if (!ipAddr.isPresent()) {
                IpAddrForTodayCnt checkCnt = IpAddrForTodayCnt.builder()
                        .ip(ip)
                        .nickname(member.getNickname())
                        .build();
                checkCnt.setTimeout(86400L);
                ipAddrForTodayCntRepository.save(checkCnt);
                memberRepository.updateTodayCnt(member.getMsrl());
                memberRepository.updateTotalCnt(member.getMsrl());
                System.out.println("Im PostList");
            }
        }

        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //사이트의 모든 블로그의 포스트 리스트(최신글)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 블로그의 포스트 리스트 최신글", notes = "사이트의 모든 블로그의 포스트 리스트 인기글")
    @GetMapping(value = "/blog/recent")
    public ListResult<ListResult> listRecentPosts(@RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();

        LocalDateTime date = LocalDateTime.now();
        date.minus(14, ChronoUnit.DAYS);
        List<OnlyPostMapping> list = postRepository.findByCreatedAtLessThanEqualAndBoard_NameNotLikeOrderByPostIdDesc(date, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        result.add(responseService.getListResult(list));
        List<String> filePaths = new ArrayList<>();
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyPostMapping pm : list) { //전체 포스트 리스트 for문
                long postId = pm.getPostId();
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                amIInTheList.add(false);
                if (postMemberRepository.findPostMemberByMember_MsrlAndPost_PostId(logined.get().getMsrl(), postId).isPresent()) {
                    amIInTheList.remove(cnt);
                    amIInTheList.add(true);
                }
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
                cnt++;
            }
        } else {
            for (OnlyPostMapping pm : list) {
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long postId = pm.getPostId();
                amIInTheList.add(false);
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        }
        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //사이트의 모든 블로그의 포스트 리스트(인기글)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 블로그의 포스트 리스트 인기글", notes = "사이트의 모든 블로그의 포스트 리스트 인기글")
    @GetMapping(value = "/blog/trend")
    public ListResult<ListResult> listTrendPosts(@RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(14, ChronoUnit.DAYS);
        List<OnlyPostMapping> list = postRepository.findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualAndBoard_NameNotLikeOrLikesGreaterThanEqualAndCreatedAtLessThanEqualAndBoard_NameNotLikeOrderByPostIdDesc(40, date, "나의 Answers", 20, date, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        result.add(responseService.getListResult(list));
        List<String> filePaths = new ArrayList<>();
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyPostMapping pm : list) { //전체 포스트 리스트 for문
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long postId = pm.getPostId();
                amIInTheList.add(false);
                if (postMemberRepository.findPostMemberByMember_MsrlAndPost_PostId(logined.get().getMsrl(), postId).isPresent()) {
                    amIInTheList.remove(cnt);
                    amIInTheList.add(true);
                }
                //파일 조회
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
                cnt++;
            }
        } else {
            for (OnlyPostMapping pm : list) {
                Member member = memberRepository.findByNickname(pm.getMember_nickname()).orElseThrow(CUserNotFoundException::new);
                long postId = pm.getPostId();
                amIInTheList.add(false);
                //파일 조회
                System.out.println(postId);
                if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
                    System.out.println(postId);
                    List<PostUploadsDto> files = postUploadsService.getList(postId);
                    PostUploadsDto file = files.get(0);
                    filePaths.add(file.getImgFullPath());
                } else {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = profileImgDto.getImgFullPath();
                    filePaths.add(filePath);
                }
            }
        }
        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }
}