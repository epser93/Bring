package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.ParamBoard;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Board.service.PostService;
import com.web.blog.Common.advice.exception.CBoardExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"F. Board"})
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final PostService postService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;

    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 카테고리 생성", notes = "게시판 카테고리 생성")
    @PostMapping(value = "/blog/{nickname}/create")
    public void createB(@Valid @RequestBody ParamBoard paramBoard, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            Optional<Board> board = Optional.ofNullable(boardRepository.findByNameAndMember(paramBoard.getName(), member));
            if (board.isPresent()) throw new CBoardExistException();
            String uid = member.getUid();
            Board board1 = boardService.createBoard(uid, paramBoard.getName());
            ParamPost paramPost = new ParamPost();
            paramPost.setSubject("First!1!Post:2:On;3;New:4:Board");
            paramPost.setOriginal((long)-1);
            paramPost.setContent("First!1!Post:2:On;3;New:4:Board");
            paramPost.setTags(null);
            Post post = postService.writePost(paramBoard.getName(), paramPost, member, "");
            boardRepository.updatePostCntMinus(board1.getBoardId());
        }
    }

    @ApiOperation(value = "게시판 카테고리 목록", notes = "게시판 카테고리 리스트")
    @GetMapping(value = "/blog/{nickname}/categories")
    public ListResult<ListResult> getBoards(@PathVariable String nickname) {
        List<ListResult> results = new ArrayList<>();
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        List<Board> boards = boardService.getBoards(member);
        results.add(responseService.getListResult(boards));
        List<Integer> postCnts = new ArrayList<>();
        for (Board b : boards) {
            int postCnt = b.getPostCnt();
            postCnts.add(postCnt);
        }
        results.add(responseService.getListResult(postCnts));
        return responseService.getListResult(results);
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
}