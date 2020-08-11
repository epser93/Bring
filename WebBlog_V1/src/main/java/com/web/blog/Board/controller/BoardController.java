package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.model.*;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.*;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.repository.ProfileImgRepository;
import com.web.blog.Member.service.ProfileImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Api(tags = {"5. Board"})
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final PostService postService;
    private final TagService tagService;
    private final SearchService searchService;
    private final ReplyService replyService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final ProfileImgService profileImgService;
    private final ProfileImgRepository profileImgRepository;
    private final PostUploadsService postUploadsService;
    private final PostUploadsRepository postUploadsRepository;

    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 카테고리 생성", notes = "게시판 카테고리 생성")
    @PostMapping(value = "/blog/{nickname}/create")
    public SingleResult<Board> createB(@Valid @RequestBody ParamBoard paramBoard, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            Optional<Board> board = Optional.ofNullable(boardRepository.findByNameAndMember(paramBoard.getName(), member));
            if (board.isPresent()) throw new CBoardExistException();
            String uid = member.getUid();
            return responseService.getSingleResult(boardService.createBoard(uid, paramBoard.getName()));
        }
        return null;
    }

    @ApiOperation(value = "게시판 카테고리 목록", notes = "게시판 카테고리 리스트")
    @GetMapping(value = "/blog/{nickname}/categories")
    public ListResult<Board> getBoards(@PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        return responseService.getListResult(boardService.getBoards(member));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 이름 수정", notes = "게시판 이름 수정")
    @PutMapping(value = "/blog/{nickname}/{boardName}")
    public SingleResult<Board> updateBoard(@PathVariable String boardName, @Valid @RequestBody ParamBoard paramBoard, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            Optional<Board> board = Optional.ofNullable(boardRepository.findByNameAndMember(paramBoard.getName(), member)); //바꿀 이름을 가진 게시판이 있는지 체크
            if (board.isPresent()) throw new CBoardExistException();
            Board brd = boardService.findBoard(boardName, member); //기존 게시판 불러오기
            return responseService.getSingleResult(boardService.updateBoard(brd.getBoardId(), member.getUid(), paramBoard.getName()));
        }
        return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 삭제", notes = "게시판 삭제")
    @DeleteMapping(value = "/blog/{nickname}/{boardName}")
    public CommonResult deleteBoard(@PathVariable String boardName, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            long msrl = member.getMsrl();
            Board board = boardService.findBoard(boardName, member);
            Long boardId = board.getBoardId();
            boardService.deleteBoard(boardId, msrl);
            return responseService.getSuccessResult();
        }
        return null;
    }

    //게시판 포스트 리스트
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 포스트 리스트", notes = "한 카테고리 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/{boardName}/post_list")
    public ListResult<ListResult> listPosts(@PathVariable String boardName, @PathVariable String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardService.findBoard(boardName, member);
        List<OnlyPostMapping> list = boardService.CategoryPostList(board.getBoardId()); //포스트 리스트
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
                }

            }
        }

        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //게시판 내 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/category/{board_id}/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable long board_id, @PathVariable(required = false) String keyword, @PathVariable String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Board board = boardService.getBoard(board_id);
        Member member = board.getMember();
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        if (nickname.equals(member.getNickname())) {
            List<OnlyPostMapping> list = searchService.CategoryPostSearch(type, board_id, keyword);
            list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
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
                        if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                            ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                            String filePath = "";
                            if(profileImgDto != null) {
                                filePath = profileImgDto.getImgFullPath();
                            }
                            filePaths.add(filePath);
                        } else {

                        }
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
                        if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                            ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                            String filePath = "";
                            if(profileImgDto != null) {
                                filePath = profileImgDto.getImgFullPath();
                            }
                            filePaths.add(filePath);
                        } else {

                        }
                    }
                }
            }
            result.add(responseService.getListResult(amIInTheList));
            result.add(responseService.getListResult(filePaths));
            return responseService.getListResult(result);
        } else return null;
    }

    //블로그 포스트 리스트
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "블로그 포스트 리스트", notes = "한 블로그 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/post_list")
    public ListResult<ListResult> listPosts(@PathVariable String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<OnlyPostMapping> list = postRepository.findAllByMember_Nickname(nickname);
        list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
                }
            }
        }

        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    //블로그 내 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "블로그 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/blogPosts/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable String nickname, @PathVariable(required = false) String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        List<OnlyPostMapping> list = searchService.BlogPostSearch(type, nickname, keyword);
        list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
        result.add(responseService.getListResult(list));
        List<String> filePaths = new ArrayList<>();
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                } else { //없으면~
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) { //프사가 있으면~
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else { //없으면~

                    }
                }
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
    public ListResult<ListResult> listRecentPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();

        LocalDateTime date = LocalDateTime.now();
        date.minus(14, ChronoUnit.DAYS);
        List<OnlyPostMapping> list = postRepository.findByCreatedAtLessThanEqualOrderByCreatedAtDesc(date);
        list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
    public ListResult<ListResult> listTrendPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        date.minus(14, ChronoUnit.DAYS);
        List<OnlyPostMapping> list = postRepository.findDistinctByViewsGreaterThanEqualAndCreatedAtLessThanEqualOrLikesGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtDesc(40, date, 20, date);
        list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
                }
            }
        }
        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }


    //사이트의 모든 블로그의 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 블로그의 포스트 검색 ", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 작성자 검색, type 4: 통합검색, ")
    @GetMapping(value = "/search/all_blog_posts/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable(required = false) String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();

        List<OnlyPostMapping> list = searchService.SitePostSearch(type, keyword);
        list.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
        result.add(responseService.getListResult(list));
        List<String> filePaths = new ArrayList<>();
        int cnt = 0;
        if (logined.isPresent()) {
            for (OnlyPostMapping pm : list) {
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
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
                    if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                        ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                        String filePath = "";
                        if(profileImgDto != null) {
                            filePath = profileImgDto.getImgFullPath();
                        }
                        filePaths.add(filePath);
                    } else {

                    }
                }
            }
        }
        result.add(responseService.getListResult(amIInTheList));
        result.add(responseService.getListResult(filePaths));
        return responseService.getListResult(result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성")
    @PostMapping(value = "/blog/{nickname}/{boardName}") //ParamPost 프론트에서 태그 중복작성 불가하게 처리 필요
    public ListResult<SingleResult> post(@PathVariable String boardName, @Valid @RequestBody ParamPost paramPost, @PathVariable String nickname) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tagSet = paramPost.getTags();
        List<String> tags = new ArrayList<>(tagSet);
        List<SingleResult> result = new ArrayList<>();

        paramPost.setOriginal((long) -1); //공유출처 없이 내가 직접 작성한 것
        Post post = null;
        if (member.equals(member2)) { //블로그 주인과 로그인 한 사용자가 같으면~
            post = postService.writePost(boardName, paramPost, member, "");
        }

        if (!tags.isEmpty()) {
            for (String tag : tags) {
                tagService.insertTags(post, tag);
            }
        }
        result.add(responseService.getSingleResult(post));
        result.add(responseService.getSingleResult(tags));
        return responseService.getListResult(result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 상세정보 조회", notes = "게시글 상세정보 비회원 조회")
    @GetMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public ListResult<ListResult> post(@PathVariable String boardName, @PathVariable long postId, @PathVariable String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = memberRepository.findByUid(uid);
        List<ListResult> results = new ArrayList<>();
        List<String> likes;
        likes = postService.likedMemberList(postId);
        boolean isLiked = false;
        if (logined.isPresent()) {
            for (String nick : likes) {
                if (nick.equals(logined.get().getNickname())) isLiked = true;
                else isLiked = false;
            }
        }
        List<Boolean> like = new ArrayList<>();
        like.add(isLiked);

        //파일 조회
        List<String> filePaths = new ArrayList<>();
        if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
            List<PostUploadsDto> files = postUploadsService.getList(postId);
            for (PostUploadsDto ud : files) {
                filePaths.add(ud.getImgFullPath());
            }
        }
        postRepository.updateViewCnt(postId);
        results.add(responseService.getListResult(postService.getPost(postId)));
        results.add(responseService.getListResult(tagService.getTags(postId)));
        results.add(responseService.getListResult(replyService.getRepliesofOnePost(postId)));
        results.add(responseService.getListResult(likes));
        results.add(responseService.getListResult(like));
        results.add(responseService.getListResult(filePaths));
        return responseService.getListResult(results);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 좋아요", notes = "게시글 좋아요")
    @PostMapping(value = "/blog/{nickname}/like/{postId}") //닉네임은 포스트 작성자
    public SingleResult<Integer> like(@RequestBody Boolean likeit, @PathVariable long postId, @PathVariable String nickname) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserNotFoundException::new); //로그인한 사용자
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Optional<PostMember> postMember = postMemberRepository.findPostMemberByMember_MsrlAndPost(member.getMsrl(), post);

        int like = 0;
        if (postMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (postMember.isPresent() && !likeit) { //좋아요를 이미 누른 사용자이면서 좋아요를 취소하면
            postService.likePost(member, post, likeit);
            List<OnlyPostMapping> findpost = postService.getPost(postId);
            OnlyPostMapping onlyPostMapping = findpost.get(0);
            like = onlyPostMapping.getLikes();
        } else {
            if (!member.getNickname().equals(nickname) && likeit) { //로그인 한 사용자가 게시글 작성자가 아니고~
                postService.likePost(member, post, likeit);
                List<OnlyPostMapping> findpost = postService.getPost(postId);
                OnlyPostMapping onlyPostMapping = findpost.get(0);
                like = onlyPostMapping.getLikes();
            } else if (member.getNickname().equals(nickname)) throw new COwnerCannotLike();
        }
        return responseService.getSingleResult(like);
    }

    @ApiOperation(value = "게시글 좋아요 유저 목록", notes = "해당 게시글의 좋아요를 누른 유저들의 목록을 보여준다.")
    @GetMapping(value = "/blog/{nickname}/{boardName}/{postId}/likedusers")
    public ListResult<String> listLiked(@PathVariable long postId, @PathVariable String boardName, @PathVariable String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardName, member);
        if (post.getBoard().equals(board)) return responseService.getListResult(postService.likedMemberList(postId));
        return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 수정", notes = "게시글 수정")
    @PutMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public ListResult<SingleResult> post(@PathVariable String boardName, @PathVariable long postId, @Valid @RequestBody ParamPost paramPost, @PathVariable String nickname) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tag1 = paramPost.getTags();
        List<String> tags = new ArrayList<>(tag1);
        List<SingleResult> result = new ArrayList<>();

        Post post = new Post();
        if (member.equals(member2)) { //블로그 주인과 로그인 한 사용자가 같으면~~
            post = postService.updatePost(boardName, postId, member.getMsrl(), paramPost);
        }
        if (!tags.isEmpty()) {
            for (String tag : tags) {
                tagService.updateTag(post, tag);
            }
        }
        result.add(responseService.getSingleResult(post));
        result.add(responseService.getSingleResult(tags));
        return responseService.getListResult(result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 삭제", notes = "게시글 삭제")
    @DeleteMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public CommonResult deletePost(@PathVariable Long postId, @PathVariable String nickname, @PathVariable String boardName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        postService.deletePost(postId, member);

        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "전체 태그 리스트", notes = "전체 태그 리스트")
    @GetMapping(value = "/tags/list")
    public ListResult<OnlyTagMapping> allTagList() {
        return responseService.getListResult(tagService.getAllTags());
    }

    @ApiOperation(value = "사용자 태그 리스트", notes = "사용자 태그 리스트")
    @GetMapping(value = "/tags/list/{msrl}")
    public ListResult<ListResult> onesTagList(@PathVariable long msrl) {
        return responseService.getListResult(tagService.getOnesTags(msrl));
    }

    //Post 공유기능
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 공유", notes = "게시글 공유")
    @PostMapping(value = "/blog/{nickname}/{boardName}/share/{postId}")
    public ListResult<SingleResult> sharePost(@PathVariable String nickname, @PathVariable String boardName, @PathVariable long postId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인 한 사용자
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new); //공유할 블로그의 주인
        ParamPost paramPost = new ParamPost();

        //postId 는 공유할 포스트의 아이디!
        Optional<OnlyPostMapping> post = postRepository.findAllByPostId(postId); //공유할 포스트 정보 불러오기
        long post_id = post.get().getPostId();
        Optional<Member> writer = memberRepository.findByNickname(post.get().getMember_nickname()); //공유된 포스트의 작성자
        if (post.get().getOriginal() != -1) throw new CSharedPostException();
        paramPost.setContent(post.get().getContent()); //포스트 인자에 공유포스트의 컨텐츠 불러와서 저장
        paramPost.setSubject(post.get().getSubject()); //포스트 인자에 공유포스트의 제목 불러와서 저장
        Set<String> tagSet = new HashSet<>(tagService.getTags(postId)); //포스트 인자에 공유포스트의 태그 불러와서 저장
        List<String> tags = new ArrayList<>(tagSet);
        List<SingleResult> result = new ArrayList<>(); //결과값 리스트 생성
        paramPost.setOriginal(postId); //공유한 원 포스트의 포스트아이디 저장
        Post share = new Post();

        if (member.equals(member2) && !member.equals(writer.get())) { //블로그 주인과 로그인 한 사용자가 같으면~ && 로그인 한 사용자와 공유할 포스트의 작성자가 다르면~
            share = postService.writePost(boardName, paramPost, member, post.get().getMember_uid());
            if (!tags.isEmpty()) {
                for (String tag : tags) {
                    tagService.insertTags(share, tag);
                }
            }
            result.add(responseService.getSingleResult(share));
            result.add(responseService.getSingleResult(tags));
            if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드 된 파일이 존재하면~
                int num = 0;
                List<PostUploadsDto> files = postUploadsService.getList(postId); //해당 파일들 불러와서
                for (PostUploadsDto postUploadsDto : files) { //각 파일들마다 DB에 저장!
                    String imgPath = postUploadsDto.getFilePath();
                    PostUploadsDto ud = new PostUploadsDto();
                    ud.setFilePath(imgPath);
                    ud.setPostId(share.getPostId());
                    ud.setNum(num);
                    postUploadsService.savePost(ud);
                    num++;
                }
            }
        }
        return responseService.getListResult(result);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 등록", notes = "새로운 포스트 작성 시, 또는 공유 할 시 파일 등록")
    @PostMapping(value = "/blog/{nickname}/{boardName}/uploads")
    public SingleResult<Boolean> upload(@PathVariable String nickname, @PathVariable String boardName, @RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Board board = boardRepository.findByNameAndMember(boardName, logined);
        Optional<List<Post>> list = postRepository.findByBoard_BoardIdAndMember_Nickname(board.getBoardId(), nickname);

        if (list.isPresent()) {
            Post post = list.get().get(list.get().size() - 1);
            long postId = post.getPostId();
            return responseService.getSingleResult(postService.saveFiles(postId, nickname, files));
        } else return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 수정 등록", notes = "기존 포스트 수정 할 때, 필요시 파일 수정")
    @PostMapping(value = "/blog/{nickname}/{boardName}/{postId}/uploads")
    public SingleResult<Boolean> uploadUpdate(@PathVariable String nickname, @PathVariable String boardName, @PathVariable long postId, @RequestPart MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member logined = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        return responseService.getSingleResult(postService.saveFiles(postId, nickname, files));
    }
}