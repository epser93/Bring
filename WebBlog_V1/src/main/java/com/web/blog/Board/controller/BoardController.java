package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.service.FileService;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.awt.print.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Api(tags = {"5. Blog"})
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final PostMemberRepository postMemberRepository;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시판 카테고리 생성", notes = "게시판 카테고리 생성")
    @PostMapping(value = "/blog/{nickname}/create")
    public SingleResult<Board> createB(@RequestBody String name, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            Optional<Board> board = Optional.ofNullable(boardRepository.findByNameAndMember(name, member));
            if (board.isPresent()) throw new CBoardExistException();
            String uid = member.getUid();
            return responseService.getSingleResult(boardService.createBoard(uid, name));
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
    public SingleResult<Board> updateBoard(@PathVariable String boardName, @Valid @RequestBody String name, @PathVariable String nickname) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = (Member) principal;
        if (member.getNickname().equals(nickname)) {
            Optional<Board> board = Optional.ofNullable(boardRepository.findByNameAndMember(name, member));
            if (board.isPresent()) throw new CBoardExistException();
            Board brd = boardService.findBoard(boardName, member);
            return responseService.getSingleResult(boardService.updateBoard(brd.getBoardId(), member.getUid(), name));
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
            boardService.deletePosts(msrl);
            boardService.deleteBoard(boardId, msrl);
            return responseService.getSuccessResult();
        }
        return null;
    }

    //게시판 포스트 리스트
    @ApiOperation(value = "게시판 포스트 리스트", notes = "한 카테고리 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/{boardName}/post_list")
    public ListResult<Post> listPosts(@PathVariable String boardName, @PathVariable String nickname, @RequestParam int page, @RequestParam int size) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardService.findBoard(boardName, member);
        Page<Post> paging = boardService.CategoryPostList(board.getBoardId(), page, size);
        List<Post> list = paging.getContent();
        if (member.getNickname().equals(nickname)) {
            return responseService.getListResult(list);
        } else return null;
    }

    //게시판 내 포스트 검색
    @ApiOperation(value = "게시판 내 검색", notes = "type 1: 통합검색, type 2: 제목 검색, type 3: 내용 검색, type 4: 태그 검색")
    @GetMapping(value = "/blog/{nickname}/search/category/{board_id}/{keyword}/{type}")
    public ListResult<Post> searchAlgorithm(@PathVariable int type, @PathVariable long board_id, @PathVariable(required = false) String keyword, @PathVariable String nickname) {
        Board board = boardService.getBoard(board_id);
        Member member = board.getMember();
        if (nickname.equals(member.getNickname())) {
            return responseService.getListResult(boardService.CategoryPostSearch(type, board_id, keyword));
        } else return null;
    }

    //블로그 포스트 리스트
    @ApiOperation(value = "블로그 포스트 리스트", notes = "한 블로그 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/post_list")
    public ListResult<Post> listPosts(@PathVariable String nickname) {
        return responseService.getListResult(boardService.BlogPostList(nickname));
    }

    //블로그 내 포스트 검색
    @ApiOperation(value = "블로그 내 검색", notes = "type 1: 통합검색, type 2: 제목 검색, type 3: 내용 검색, type 4: 태그 검색")
    @GetMapping(value = "/blog/{nickname}/search/blogPosts/{keyword}/{type}")
    public ListResult<Post> searchAlgorithm(@PathVariable int type, @PathVariable String nickname, @PathVariable(required = false) String keyword) {
        return responseService.getListResult(boardService.BlogPostSearch(type, nickname, keyword));
    }

    //사이트의 모든 블로그의 포스트 리스트
    @ApiOperation(value = "모든 블로그의 포스트 리스트", notes = "사이트의 모든 블로그의 포스트 리스트")
    @GetMapping(value = "/search/all_blog_posts")
    public ListResult<Post> listPosts() {
        return responseService.getListResult(boardService.SitePostList());
    }

    //사이트의 모든 블로그의 포스트 검색
    @ApiOperation(value = "모든 블로그의 포스트 검색 ", notes = "type 1: 통합검색, type 2: 제목 검색, type 3: 내용 검색, type 4: 태그 검색, type 5: 작성자 검색")
    @GetMapping(value = "/search/all_blog_posts/{keyword}/{type}")
    public ListResult<Post> searchAlgorithm(@PathVariable int type, @PathVariable(required = false) String keyword) {
        return responseService.getListResult(boardService.SitePostSearch(type, keyword));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성")
    @PostMapping(value = "/blog/{nickname}/{boardName}")
    public SingleResult<Post> post(@PathVariable String boardName, @Valid @RequestBody ParamPost post, @PathVariable String nickname, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException { //, @RequestParam("files") MultipartFile[] files
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        if (member.equals(member2)) { //블로그 주인과 로그인 한 사용자가 같으면~
            return responseService.getSingleResult(boardService.writePost(member.getNickname(), boardName, post, files, member2));
        }
        return null;
    }

    @ApiOperation(value = "게시글 상세정보 조회", notes = "게시글 상세정보 비회원 조회")
    @GetMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public SingleResult<Post> post(@PathVariable String boardName, @PathVariable long postId, @PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardName, member);
        return responseService.getSingleResult(boardService.getPost(postId, board));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 좋아요", notes = "게시글 좋아요")
    @PostMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public void like(@RequestBody Boolean likeit, @PathVariable long postId, @PathVariable String nickname, @PathVariable String boardName) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Optional<PostMember> postMember = postMemberRepository.findPostMemberByMember_MsrlAndPost(member.getMsrl(), post);
        if (postMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (postMember.isPresent() && !likeit) { //좋아요를 이미 누른 사용자이면서 좋아요를 취소하면
            boardService.likePost(member, post, likeit);
        } else {
            if (!member.getNickname().equals(nickname) && likeit) { //로그인 한 사용자가 게시글 작성자가 아니고~
                boardService.likePost(member, post, likeit);
            } else if (member.getNickname().equals(nickname)) throw new COwnerCannotLike();
        }
    }

    @ApiOperation(value = "게시글 좋아요 유저 목록", notes = "해당 게시글의 좋아요를 누른 유저들의 목록을 보여준다.")
    @GetMapping(value = "/blog/{nickname}/{boardName}/{postId}/likedusers")
    public ListResult<String> listLiked(@PathVariable long postId, @PathVariable String boardName, @PathVariable String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardName, member);
        if (post.getBoard().equals(board)) return responseService.getListResult(boardService.likedMemberList(post));
        return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 수정", notes = "게시글 수정")
    @PutMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public SingleResult<Post> post(@PathVariable String boardName, @PathVariable long postId, @Valid @RequestBody ParamPost post, @PathVariable String nickname, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        if (member.equals(member2)) {
            return responseService.getSingleResult(boardService.updatePost(boardName, postId, member.getMsrl(), post, files));
        }
        return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 삭제", notes = "게시글 삭제")
    @DeleteMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public CommonResult deletePost(@PathVariable long postId, @PathVariable String nickname, @PathVariable String boardName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        boardService.deletePost(postId, uid);
        return responseService.getSuccessResult();
    }

}