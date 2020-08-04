package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.ParamBoard;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            boardService.deletePosts(msrl);
            boardService.deleteBoard(boardId, msrl);
            return responseService.getSuccessResult();
        }
        return null;
    }

    //게시판 포스트 리스트
    @ApiOperation(value = "게시판 포스트 리스트", notes = "한 카테고리 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/{boardName}/post_list")
    public ListResult<OnlyPostMapping> listPosts(@PathVariable String boardName, @PathVariable String nickname) {
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardService.findBoard(boardName, member);
        List<OnlyPostMapping> list = boardService.CategoryPostList(board.getBoardId());
        if (member.getNickname().equals(nickname)) {
            return responseService.getListResult(list);
        } else return null;
    }

    //게시판 내 포스트 검색
    @ApiOperation(value = "게시판 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/category/{board_id}/{keyword}/{type}")
    public ListResult<OnlyPostMapping> searchAlgorithm(@PathVariable int type, @PathVariable long board_id, @PathVariable(required = false) String keyword, @PathVariable String nickname) {
        Board board = boardService.getBoard(board_id);
        Member member = board.getMember();
        if (nickname.equals(member.getNickname())) {
            return responseService.getListResult(searchService.CategoryPostSearch(type, board_id, keyword));
        } else return null;
    }

    //블로그 포스트 리스트
    @ApiOperation(value = "블로그 포스트 리스트", notes = "한 블로그 내 모든 포스트 리스트")
    @GetMapping(value = "/blog/{nickname}/post_list")
    public ListResult<OnlyPostMapping> listPosts(@PathVariable String nickname) {
        return responseService.getListResult(postRepository.findAllByWriter(nickname));
    }

    //블로그 내 포스트 검색
    @ApiOperation(value = "블로그 내 검색", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 통합검색")
    @GetMapping(value = "/blog/{nickname}/search/blogPosts/{keyword}/{type}")
    public ListResult<OnlyPostMapping> searchAlgorithm(@PathVariable int type, @PathVariable String nickname, @PathVariable(required = false) String keyword) {
        return responseService.getListResult(searchService.BlogPostSearch(type, nickname, keyword));
    }

    //사이트의 모든 블로그의 포스트 리스트
    @ApiOperation(value = "모든 블로그의 포스트 리스트", notes = "사이트의 모든 블로그의 포스트 리스트")
    @GetMapping(value = "/search/all_blog_posts")
    public ListResult<OnlyPostMapping> listPosts() {
        Long num = (long) 0;
        return responseService.getListResult(postRepository.findAllByPostIdGreaterThan(num));
    }

    //사이트의 모든 블로그의 포스트 검색
    @ApiOperation(value = "모든 블로그의 포스트 검색 ", notes = "type 1: 제목 검색, type 2: 내용 검색, type 3: 작성자 검색, type 4: 통합검색, ")
    @GetMapping(value = "/search/all_blog_posts/{keyword}/{type}")
    public ListResult<OnlyPostMapping> searchAlgorithm(@PathVariable int type, @PathVariable(required = false) String keyword) {
        return responseService.getListResult(searchService.SitePostSearch(type, keyword));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성")
    @PostMapping(value = "/blog/{nickname}/{boardName}") //ParamPost 프론트에서 태그 중복작성 불가하게 처리 필요
    public ListResult<SingleResult> post(@PathVariable String boardName, @Valid @RequestBody ParamPost paramPost, @PathVariable String nickname, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException { //, @RequestParam("files") MultipartFile[] files
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tag1 = paramPost.getTags();
        List<String> tags = new ArrayList<>(tag1);
        List<SingleResult> result = new ArrayList<>();
        Post post = null;
        if (member.equals(member2)) { //블로그 주인과 로그인 한 사용자가 같으면~
            post = postService.writePost(member.getNickname(), boardName, paramPost, files, member2);
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
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardName, member);
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        List<ListResult> results = new ArrayList<>();
        List<String> likes;
        likes = postService.likedMemberList(post);
        boolean isLiked = false;
        if(logined.isPresent()){
            for(String nick : likes) {
                if(nick.equals(logined.get().getNickname())) isLiked = true;
                else isLiked = false;
            }
        }
        List<Boolean> like = new ArrayList<>();
        like.add(isLiked);
        results.add(responseService.getListResult(postService.getPost(postId)));
        results.add(responseService.getListResult(tagService.getTags(postId)));
        results.add(responseService.getListResult(replyService.getRepliesofOnePost(postId)));
        results.add(responseService.getListResult(likes));
        results.add(responseService.getListResult(like));
        return responseService.getListResult(results);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 좋아요", notes = "게시글 좋아요")
    @PostMapping(value = "/blog/{nickname}/like/{postId}")
    public void like(@RequestBody Boolean likeit, @PathVariable long postId, @PathVariable String nickname) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인한 사용자
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Optional<PostMember> postMember = postMemberRepository.findPostMemberByMember_MsrlAndPost(member.getMsrl(), post);
        if (postMember.isPresent() && likeit) {
            throw new CAlreadyLikedException();
        } else if (postMember.isPresent() && !likeit) { //좋아요를 이미 누른 사용자이면서 좋아요를 취소하면
            postService.likePost(member, post, likeit);
        } else {
            if (!member.getNickname().equals(nickname) && likeit) { //로그인 한 사용자가 게시글 작성자가 아니고~
                postService.likePost(member, post, likeit);
            } else if (member.getNickname().equals(nickname)) throw new COwnerCannotLike();
        }
    }

    @ApiOperation(value = "게시글 좋아요 유저 목록", notes = "해당 게시글의 좋아요를 누른 유저들의 목록을 보여준다.")
    @GetMapping(value = "/blog/{nickname}/{boardName}/{postId}/likedusers")
    public ListResult<String> listLiked(@PathVariable long postId, @PathVariable String boardName, @PathVariable String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardName, member);
        if (post.getBoard().equals(board)) return responseService.getListResult(postService.likedMemberList(post));
        return null;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 수정", notes = "게시글 수정")
    @PutMapping(value = "/blog/{nickname}/{boardName}/{postId}")
    public ListResult<SingleResult> post(@PathVariable String boardName, @PathVariable long postId, @Valid @RequestBody ParamPost paramPost, @PathVariable String nickname, @RequestParam(value = "files", required = false) MultipartFile[] files) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new);
        Member member2 = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tag1 = paramPost.getTags();
        List<String> tags = new ArrayList<>(tag1);
        List<SingleResult> result = new ArrayList<>();
        Post post = null;
        if (member.equals(member2)) { //블로그 주인과 로그인 한 사용자가 같으면~~
            post = postService.updatePost(boardName, postId, member.getMsrl(), paramPost, files);
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
}