package com.web.blog.Board.controller;

import com.web.blog.Board.model.OnlyTagMapping;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.*;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.repository.IpAddrForTodayCntRepository;
import com.web.blog.Member.repository.IpAddrForViewCntRepository;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.repository.ProfileImgRepository;
import com.web.blog.Member.service.FollowService;
import com.web.blog.Member.service.ProfileImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = {"H. Post Tag"})
@RequiredArgsConstructor
@RestController
public class PostTagController {

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
    private final FollowService followService;
    private final IpAddrForTodayCntRepository ipAddrForTodayCntRepository;
    private final IpAddrForViewCntRepository ipAddrForViewCntRepository;
    private final S3Service s3Service;

    @ApiOperation(value = "전체 태그 리스트", notes = "전체 태그 리스트")
    @GetMapping(value = "/tags/list")
    public ListResult<ListResult> allTagList() {
        List<ListResult> results = new ArrayList<>();
        List<OnlyTagMapping> list = tagService.getAllTags();
        results.add(responseService.getListResult(list));
        List<Integer> cnts = new ArrayList<>();
        for (OnlyTagMapping otm : list) {
            cnts.add(otm.getTagUsageCnt());
        }
        results.add(responseService.getListResult(cnts));
        return responseService.getListResult(results);
    }

    @ApiOperation(value = "사용자 태그 리스트", notes = "사용자 태그 리스트")
    @GetMapping(value = "/tags/list/{msrl}")
    public ListResult<ListResult> onesTagList(@PathVariable long msrl) {
        return responseService.getListResult(tagService.getOnesTags(msrl));
    }

    @ApiOperation(value = "사용자 포스트 태그 리스트", notes = "사용자 포스트 태그 리스트")
    @GetMapping(value = "/tags/blog/{nickname}")
    public ListResult<ListResult> onesBlogTags(@PathVariable String nickname) {
        return responseService.getListResult(tagService.getOnesBlogTags(nickname));
    }

    @ApiOperation(value = "사용자 질문글 태그 리스트", notes = "사용자 질문글 태그 리스트")
    @GetMapping(value = "/tags/qna/{nickname}")
    public ListResult<ListResult> onesQnaTags(@PathVariable String nickname) {
        return responseService.getListResult(tagService.getOnesQuestionTags(nickname));
    }

    @ApiOperation(value = "전체 포스트 태그 리스트", notes = "전체 포스트 태그 리스트")
    @GetMapping(value = "/tags/blog")
    public ListResult<ListResult> allBlogTags() {
        return responseService.getListResult(tagService.getAllBlogTags());
    }

    @ApiOperation(value = "전체 질문글 태그 리스트", notes = "전체 질문글 태그 리스트")
    @GetMapping(value = "/tags/qna")
    public ListResult<ListResult> allQnaTags() {
        return responseService.getListResult(tagService.getAllQuestionTags());
    }
}