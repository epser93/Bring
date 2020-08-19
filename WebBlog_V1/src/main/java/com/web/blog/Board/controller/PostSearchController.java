package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Board.service.PostUploadsService;
import com.web.blog.Board.service.SearchService;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.model.Paging;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.ProfileImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"K. Post Search"})
@RequiredArgsConstructor
@RestController
public class PostSearchController {

    private final BoardService boardService;
    private final SearchService searchService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final ProfileImgService profileImgService;
    private final PostUploadsService postUploadsService;
    private final PostUploadsRepository postUploadsRepository;

    //게시판 내 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/category/{board_id}/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable long board_id, @PathVariable(required = false) String keyword, @PathVariable String nickname, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Board board = boardService.getBoard(board_id);
        Member member = board.getMember();
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        if (nickname.equals(member.getNickname())) {
            List<OnlyPostMapping> list = searchService.CategoryPostSearch(type, board_id, keyword, paging);
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
        } else return null;
    }

    //블로그 내 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "블로그 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/blogPosts/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable String nickname, @PathVariable(required = false) String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        List<OnlyPostMapping> list = searchService.BlogPostSearch(type, nickname, keyword, paging);
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

    //사이트의 모든 블로그의 포스트 검색
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 블로그의 포스트 검색 ", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 작성자 검색, type 4: 통합검색, ")
    @GetMapping(value = "/search/all_blog_posts/{keyword}/{type}")
    public ListResult<ListResult> searchAlgorithm(@PathVariable int type, @PathVariable(required = false) String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();

        List<OnlyPostMapping> list = searchService.SitePostSearch(type, keyword, paging);
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "전체 포스트 태그 검색", notes = "전체 포스트 태그로 검색")
    @PostMapping(value = "/blog/search/tags/{keyword}")
    public ListResult<ListResult> searchAllPostsByTag(@PathVariable String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<OnlyPostMapping> list = searchService.AllBlogTagSearch(keyword, paging);
        result.add(responseService.getListResult(list));
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "특정 유저 포스트 태그 검색", notes = "특정 유저의 포스트 태그로 검색")
    @PostMapping(value = "/blog/{nickname}/search/tags/{keyword}")
    public ListResult<ListResult> searchOnesPostsByTag(@PathVariable String nickname, @PathVariable String keyword, @RequestParam(required = false, defaultValue = "1") long no) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = Optional.ofNullable(memberRepository.findAllByUid(uid));
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Paging paging = new Paging(no);
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        List<OnlyPostMapping> list = searchService.OnesBlogTagSearch(nickname, keyword, paging);
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
}