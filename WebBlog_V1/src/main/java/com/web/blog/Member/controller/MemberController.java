package com.web.blog.Member.controller;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Board.service.PostService;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.entity.UploadFile;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.FileUploadResponse;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.FileService;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.ParamMember;
import com.web.blog.Member.repository.MemberRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Api(tags = {"2. Member"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/member")
public class MemberController {
    private final PostMemberRepository postMemberRepository;
    private final MemberRepository repository;
    private final BoardService boardService;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final PostService postService;
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원 조회")
    @GetMapping("/users")
    public ListResult<Member> findAllMember() {
//        Sort sort;
//        sort = sort.and(new Sort(Sort.Direction.ASC, "score"));
//        return responseService.getListResult(repository.findAll(sort));
        return responseService.getListResult(repository.findAll());
    }

    @ApiOperation(value = "회원 프로필 조회", notes = "닉네임으로 회원을 조회한다")
    @GetMapping(value = "/{nickname}/profile")
    public SingleResult<Member> findUserById(@PathVariable String nickname) {
        Member member = repository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        //유저가 좋아요 한 글 개수
        repository.updateLikeCnt(postMemberRepository.likedPostCnt(member.getMsrl()), member.getMsrl());
        return responseService.getSingleResult(repository.findByUid(member.getUid()).orElseThrow(CUserNotFoundException::new));
    }

    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "마이페이지 조회", notes = "마이페이지 조회")
    @GetMapping("/mypage")
    public SingleResult<Member> mypage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member logined = repository.findByUid(id).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(logined);
    }

    @ApiOperation(value = "좋아요 글 목록", notes = "좋아요한 글의 목록을 보여준다.")
    @GetMapping(value = "/{nickname}/likedposts")
    public List<Post> listLiked(@PathVariable String nickname) {
        Member member = repository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new);
        List<Post> list = postService.likedPostList(member);
        return list;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다")
    @PutMapping(value = "/update")
    public SingleResult<Member> modify(@Valid @RequestBody ParamMember paramMember) {
        String password = paramMember.getPassword1();
        String passwordChk = paramMember.getPassword2();
        String nickname = paramMember.getNickname();
        MultipartFile file = paramMember.getUploadFile();

        //패스워드 체크
        if (!password.equals(passwordChk)) {
            return (SingleResult<Member>) responseService.getFailResult(-1001, "비밀번호가 다릅니다.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = repository.findByUid(id).orElseThrow(CUserNotFoundException::new);
        member.setPassword(passwordEncoder.encode(password));
        member.setNickname(nickname);

        if (file != null) {
            UploadFile fileName = fileService.uploadFile(file);
            member.setUploadfile(fileName);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/")
                    .path(fileName.getFilename())
                    .toUriString();
            FileUploadResponse fileUploadResponse = new FileUploadResponse(fileName.getFilename(), fileDownloadUri, file.getContentType(), file.getSize());
        }
        return responseService.getSingleResult(repository.save(member));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원번호로 회원정보를 삭제한다")
    @DeleteMapping(value = "/delete/{msrl}")
    public CommonResult delete(@ApiParam(value = "회원번호", required = true) @PathVariable long msrl) {
        boardService.deletePosts(msrl);
        boardService.deleteBoards(msrl);
        repository.deleteById(msrl);
        return responseService.getSuccessResult();
    }
}
