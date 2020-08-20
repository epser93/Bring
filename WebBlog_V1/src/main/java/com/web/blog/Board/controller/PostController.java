package com.web.blog.Board.controller;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.entity.PostUploads;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Board.service.PostService;
import com.web.blog.Board.service.PostUploadsService;
import com.web.blog.Board.service.ReplyService;
import com.web.blog.Board.service.TagService;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.IpAddrForTodayCnt;
import com.web.blog.Member.entity.IpAddrForViewCnt;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.IpAddrForTodayCntRepository;
import com.web.blog.Member.repository.IpAddrForViewCntRepository;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Api(tags = {"G. Post"})
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final PostService postService;
    private final TagService tagService;
    private final ReplyService replyService;
    private final ResponseService responseService;
    private final MemberRepository memberRepository;
    private final PostMemberRepository postMemberRepository;
    private final PostUploadsService postUploadsService;
    private final PostUploadsRepository postUploadsRepository;
    private final FollowService followService;
    private final IpAddrForTodayCntRepository ipAddrForTodayCntRepository;
    private final IpAddrForViewCntRepository ipAddrForViewCntRepository;
    private final S3Service s3Service;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성")
    @PostMapping(value = "/blog/{nickname}/{boardName}") //ParamPost 프론트에서 태그 중복작성 불가하게 처리 필요
    public ListResult<SingleResult> post(@PathVariable String boardName, @Valid @RequestBody ParamPost paramPost, @PathVariable String nickname) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = memberRepository.findByUid(uid);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tagSet = paramPost.getTags();
        List<String> tags = new ArrayList<>(tagSet);
        List<SingleResult> result = new ArrayList<>();

        paramPost.setOriginal((long) -1); //공유출처 없이 내가 직접 작성한 것
        Post post = null;
        if (logined.get().getMsrl() == member.getMsrl()) {
            post = postService.writePost(boardName, paramPost, logined.get(), "");
        }
        List<PostUploads> list = postUploadsRepository.findAllByNickname(logined.get().getNickname());
        String content = post.getContent();
        int num = 0;
        for (PostUploads pu : list) {
            String filep = pu.getFilePath();
            if (pu.getPostId() == -100 && !postRepository.findByPostIdAndContentContaining(post.getPostId(), filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                postUploadsRepository.deleteById(pu.getId());
            } else if (pu.getPostId() == -100 && postRepository.findByPostIdAndContentContaining(post.getPostId(), filep).isPresent()) {
                String original = pu.getFilePath();
                String rename = s3Service.rename(filep, pu.getFileName(), post.getPostId(), num, logined.get().getNickname());
                postUploadsRepository.updateFilePath(rename, pu.getId());
                postUploadsRepository.updatePostId(post.getPostId(), pu.getId());
                content = content.replace(original, rename);
                num++;
            }
        }
        postRepository.updateContent(content, post.getPostId());

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
    public ListResult<ListResult> post(@PathVariable String boardName, @PathVariable long postId, @PathVariable String nickname, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = memberRepository.findByUid(uid);
        Optional<Member> writer = memberRepository.findByNickname(nickname);
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
        if (logined.isPresent() && logined.get().getMsrl() != writer.get().getMsrl()) {
            String ip = followService.getIpAddr(request);
            Optional<IpAddrForViewCnt> ipAddrQ = ipAddrForViewCntRepository.findByIpAndPostId(ip, postId);
            if (!ipAddrQ.isPresent()) {
                IpAddrForViewCnt checkCnt = IpAddrForViewCnt.builder()
                        .ip(ip)
                        .postId(postId)
                        .qpostId((long) -1)
                        .build();
                checkCnt.setTimeout(10800L);
                ipAddrForViewCntRepository.save(checkCnt);
                postRepository.updateViewCnt(postId);
            }
            Optional<IpAddrForTodayCnt> ipAddr = ipAddrForTodayCntRepository.findByIpAndNickname(ip, writer.get().getNickname());
            if (!ipAddr.isPresent()) {
                IpAddrForTodayCnt checkCnt = IpAddrForTodayCnt.builder()
                        .ip(ip)
                        .nickname(writer.get().getNickname())
                        .build();
                checkCnt.setTimeout(43200L);
                ipAddrForTodayCntRepository.save(checkCnt);
                memberRepository.updateTodayCnt(writer.get().getMsrl());
                memberRepository.updateTotalCnt(writer.get().getMsrl());
                System.out.println("Im Post");
            }
        }
        List<OnlyPostMapping> post = postService.getPost(postId);
        OnlyPostMapping p = post.get(0);
        List<Boolean> isShared = new ArrayList<>();
        if(p.getOriginal() != -1) {
            isShared.add(true);
        } else isShared.add(false);
        results.add(responseService.getListResult(post));
        results.add(responseService.getListResult(tagService.getTags(postId)));
        results.add(responseService.getListResult(replyService.getRepliesofOnePost(postId)));
        results.add(responseService.getListResult(likes));
        results.add(responseService.getListResult(like));
        results.add(responseService.getListResult(filePaths));
        results.add(responseService.getListResult(isShared));
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
        Optional<Member> logined = memberRepository.findByUid(uid);
        Member member = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        Set<String> tag1 = paramPost.getTags();
        List<String> tags = new ArrayList<>(tag1);
        List<SingleResult> result = new ArrayList<>();

        Post post = new Post();
        if (logined.get().getMsrl() == member.getMsrl()) {
            post = postService.updatePost(boardName, postId, logined.get().getMsrl(), paramPost);
        }
        List<PostUploads> list = postUploadsRepository.findAllByNickname(logined.get().getNickname());
        String content = post.getContent();
//        int num = 0;
        for (PostUploads pu : list) {
            String filep = pu.getFilePath();
            if (pu.getPostId() == postId && !postRepository.findByPostIdAndContentContaining(postId, filep).isPresent()) { //db에 저장된 파일 경로가 해당 포스트의 내용에 포함되어 있지 않으면~
                s3Service.delete(filep);
                postUploadsRepository.deleteById(pu.getId());
            }
//            else if (postRepository.findByPostIdAndContentContaining(post.getPostId(), filep).isPresent()) {
//                String original = pu.getFilePath();
//                String rename = s3Service.rename(filep, pu.getFileName(), post.getPostId(), num, logined.get().getNickname());
//                postUploadsRepository.updateFilePath(rename, pu.getId());
//                postUploadsRepository.updatePostId(post.getPostId(), pu.getId());
//                content = content.replace(original, rename);
//                num++;
//            }
        }

        if (!tags.isEmpty()) {
            tagService.deleteTags(post);
            for (String tag : tags) {
                tagService.insertTags(post, tag);
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

    //Post 공유기능
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게시글 공유", notes = "게시글 공유")
    @PostMapping(value = "/blog/{boardName}/share/{postId}")
    public ListResult<SingleResult> sharePost(@PathVariable String boardName, @PathVariable long postId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Member member = memberRepository.findByUid(uid).orElseThrow(CUserExistException::new); //로그인 한 사용자
        ParamPost paramPost = new ParamPost();

        //postId 는 공유할 포스트의 아이디!
        Optional<OnlyPostMapping> post = postRepository.findAllByPostId(postId); //공유할 포스트 정보 불러오기
        Member member2 = memberRepository.findByNickname(post.get().getMember_nickname()).orElseThrow(CUserNotFoundException::new); //공유할 블로그의 주인
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
            if (postUploadsRepository.findByPostId(postId).isPresent()) { //업로드한 파일이 하나라도 존재하면~
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
    public ListResult<String> upload(@PathVariable String nickname, @PathVariable String boardName, @RequestPart MultipartFile[] files) throws IOException {
        return responseService.getListResult(postService.saveFiles(-100, nickname, files));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "파일 수정 등록", notes = "기존 포스트 수정 할 때, 필요시 파일 수정")
    @PostMapping(value = "/blog/{nickname}/{boardName}/{postId}/uploads")
    public ListResult<String> uploadUpdate(@PathVariable String nickname, @PathVariable String boardName, @PathVariable long postId, @RequestPart MultipartFile[] files) throws IOException {
        return responseService.getListResult(postService.saveFiles(postId, nickname, files));
    }
}