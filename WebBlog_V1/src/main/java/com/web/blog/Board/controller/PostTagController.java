package com.web.blog.Board.controller;

import com.web.blog.Board.model.OnlyTagMapping;
import com.web.blog.Board.service.TagService;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
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

    private final TagService tagService;
    private final ResponseService responseService;

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