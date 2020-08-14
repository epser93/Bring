package com.web.blog.Member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Board.service.PostService;
import com.web.blog.Common.advice.exception.CNicknameExistException;
import com.web.blog.Common.advice.exception.CPasswordDoesntMatch;
import com.web.blog.Common.advice.exception.CPasswordLengthException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.config.security.JwtTokenProvider;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.response.SingleResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.entity.ProfileImg;
import com.web.blog.Member.model.OnlyMemberMapping;
import com.web.blog.Member.model.ParamPassword;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.repository.ProfileImgRepository;
import com.web.blog.Member.service.FollowService;
import com.web.blog.Member.service.ProfileImgService;
import com.web.blog.QnA.model.OnlyApostMapping;
import com.web.blog.QnA.model.OnlyQpostMapping;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
    private final PostService postService;
    private final FollowService followService;
    private final S3Service s3Service;
    private final ProfileImgService profileImgService;
    private final ProfileImgRepository profileImgRepository;
    private final PostRepository postRepository;
    private final QpostRepository qpostRepository;
    private final ApostRepository apostRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원 조회")
    @GetMapping("/users")
    public ListResult<Member> findAllMember() {
        return responseService.getListResult(repository.findAll());
    }

    @ApiOperation(value = "회원 랭킹 조회", notes = "포인트 랭킹 조회")
    @GetMapping("/rank")
    public ListResult<Map<String, Integer>> rank() {
        return responseService.getListResult(repository.rank());
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = false, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 프로필 조회", notes = "닉네임으로 회원을 조회한다")
    @GetMapping(value = "/{nickname}/profile")
    public ListResult<ListResult> findUserById(@PathVariable String nickname, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String uid = authentication.getName();
        Optional<Member> logined = repository.findByUid(uid); //로그인 한 사용자
        Member member = repository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new); //조회하려는 멤버
        Optional<OnlyMemberMapping> omm = repository.findAllByNickname(nickname); //조회하려는 멤버의 OnlyMemberMapping 값
        List<ListResult> result = new ArrayList<>();
        List<Boolean> amIInTheList = new ArrayList<>(); //불린 값
        Boolean amIIn = false;
        List<OnlyMemberMapping> profile = new ArrayList<>(); //조회하려는 회원 정보
        profile.add(omm.get());
        List<String> img = new ArrayList<>();
        List<LocalDateTime> createdAt = new ArrayList<>();
        List<OnlyPostMapping> posts = postRepository.findAllByMember_Nickname(member.getNickname());
        posts.removeIf(opm -> opm.getBoard_name().equals("나의 Answers"));
        List<OnlyQpostMapping> qposts = qpostRepository.findByMember_Nickname(member.getNickname());
        List<OnlyApostMapping> aposts = apostRepository.findAllByMember_Nickname(member.getNickname());
        for(OnlyPostMapping opm : posts) {
            createdAt.add(opm.getCreatedAt());
        }
        for(OnlyQpostMapping oqm : qposts) {
            createdAt.add(oqm.getCreatedAt());
        }
        for(OnlyApostMapping oam : aposts) {
            createdAt.add(oam.getCreatedAt());
        }

        if(logined.isPresent()) {
            if(logined.get().getMsrl() != member.getMsrl()) { //프로필 주인이 아니면~
                amIIn = followService.isFollowed(logined.get(), member); //로그인 사용자가 조회하려는 유저를 팔로우했으면 true, 아니면 false
                amIInTheList.add(amIIn);
                result.add(responseService.getListResult(profile)); //조회하려는 멤버와 불린값 맵 설정
                result.add(responseService.getListResult(amIInTheList));
                result.add(responseService.getListResult(followService.followingList(member))); //팔로잉 리스트(조회하려는 멤버가 구독중인 멤버 리스트)
                result.add(responseService.getListResult(followService.followersList(member))); //팔로워 리스트(조회하려는 멤버를 구독중인 멤버 리스트)
                result.add(responseService.getListResult(createdAt)); //모든 포스트의 각 게시 시간
                if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = "";
                    if(profileImgDto != null) {
                        filePath = profileImgDto.getImgFullPath();
                    }
                    img.add(filePath);
                    result.add(responseService.getListResult(img)); //프로필 사진
                }
            } else { //프로필 본인이면~(마이페이지)
                result.add(responseService.getListResult(profile)); //내 정보
                amIInTheList.add(false);
                result.add(responseService.getListResult(amIInTheList));
                result.add(responseService.getListResult(followService.followingList(member))); //팔로잉 리스트(조회하려는 멤버가 구독중인 멤버 리스트)
                result.add(responseService.getListResult(followService.followersList(member))); //팔로워 리스트(조회하려는 멤버를 구독중인 멤버 리스트)
                result.add(responseService.getListResult(createdAt)); //모든 포스트의 각 게시 시간
                if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                    ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                    String filePath = "";
                    if(profileImgDto != null) {
                        filePath = profileImgDto.getImgFullPath();
                    }
                    img.add(filePath);
                    result.add(responseService.getListResult(img)); //프로필 사진
                }
            }
        } else {
            result.add(responseService.getListResult(profile)); //조회하려는 멤버 설정
            amIInTheList.add(false);
            result.add(responseService.getListResult(amIInTheList));
            result.add(responseService.getListResult(followService.followingList(member))); //팔로잉 리스트(조회하려는 멤버가 구독중인 멤버 리스트)
            result.add(responseService.getListResult(followService.followersList(member))); //팔로워 리스트(조회하려는 멤버를 구독중인 멤버 리스트)
            result.add(responseService.getListResult(createdAt)); //모든 포스트의 각 게시 시간
            if(profileImgRepository.findByMsrl(member.getMsrl()).isPresent()) {
                ProfileImgDto profileImgDto = profileImgService.getOneImg(member.getMsrl());
                String filePath = "";
                if(profileImgDto != null) {
                    filePath = profileImgDto.getImgFullPath();
                }
                img.add(filePath);
                result.add(responseService.getListResult(img)); //프로필 사진
            }
        }

        Cookie[] cookies = null;
        if(logined.isPresent() && logined.get().getMsrl() != member.getMsrl()) {
            cookies = request.getCookies();
            Map map = new HashMap();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    map.put(cookie.getName(), cookie.getValue());
                }
            }
            String key = logined.get().getMsrl() + "|" + "today_cnt";
            String cookieCnt = (String) map.get(key);
            String newCookieCnt = logined.get().getNickname() + "|" + member.getNickname();
            if (StringUtils.indexOfIgnoreCase(cookieCnt, newCookieCnt) == -1) {
                Cookie cookie = new Cookie(key, newCookieCnt);
                cookie.setMaxAge(60 * 60 * 24); //24시간
                response.addCookie(cookie);
                repository.updateTodayCnt(member.getMsrl());
                repository.updateTotalCnt(member.getMsrl());
            }
        } else if (!logined.isPresent()) {
            cookies = request.getCookies();
            Map map = new HashMap();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    map.put(cookie.getName(), cookie.getValue());
                }
            }
            String key = "temp" + "|" + "today_cnt";
            String cookieCnt = (String) map.get(key);
            String newCookieCnt = "temp" + "|" + member.getNickname();
            if (StringUtils.indexOfIgnoreCase(cookieCnt, newCookieCnt) == -1) {
                Cookie cookie = new Cookie(key, newCookieCnt);
                cookie.setMaxAge(60 * 60 * 24); //24시간
                response.addCookie(cookie);
                repository.updateTodayCnt(member.getMsrl());
                repository.updateTotalCnt(member.getMsrl());
            }
        }

        List<Integer> visitorCnt = new ArrayList<>();
        visitorCnt.add(member.getTodayCnt());
        visitorCnt.add(member.getTotalCnt());
        result.add(responseService.getListResult(visitorCnt));
        //유저가 좋아요 한 글 개수
        repository.updateLikeCnt(postMemberRepository.likedPostCnt(member.getMsrl()), member.getMsrl());

        return responseService.getListResult(result); //출력
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
    public SingleResult<Member> modify(@Valid @RequestBody ParamPassword paramMember) throws JsonProcessingException {
        String oldPassword = paramMember.getPassword3();
        Optional<String> newPassword = Optional.ofNullable(paramMember.getPassword1());
        Optional<String> newPasswordChk = Optional.ofNullable(paramMember.getPassword2());
        Optional<String> nickname = Optional.ofNullable(paramMember.getNickname());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member member = repository.findByUid(id).orElseThrow(CUserNotFoundException::new);

        if(!passwordEncoder.matches(oldPassword, member.getPassword())){ //기존 비밀번호 입력값이 DB의 값과 다르면~
            throw new CPasswordDoesntMatch(); //에러메세지
        } else { //기존 비밀번호 입력값이 DB의 값과 같으면~
            //비밀번호 변경
            if(newPassword.isPresent()) { //새 비밀번호 입력값이 비어있지 않으면~
                if(!newPasswordChk.isPresent() || newPassword.get().length() < 7 || newPassword.get().length() > 20 || newPasswordChk.get().length() < 7 || newPasswordChk.get().length() > 20) { //길이가 맞지 않으면~
                    throw new CPasswordLengthException();
                }
                if(!newPassword.get().equals(newPasswordChk.get())) { //비밀번호 확인 값과 다르면~
                    throw new CPasswordDoesntMatch();
                } else { //비밀번호 확인 값과 같으면~
                    member.setPassword(passwordEncoder.encode(newPassword.get()));
                }
            }

            //닉네임 변경(값 존재하면!)
            if(nickname.isPresent()) {
                if(!repository.findByNickname(nickname.get()).isPresent()) member.setNickname(nickname.get());
                else throw new CNicknameExistException();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String Json;
        Json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseService.getMapResult(jwtTokenProvider.createToken(String.valueOf(member.getMsrl()), member.getRoles()), member));
        return responseService.getSingleResult(repository.save(member));
    }

    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "프로필 이미지 등록", notes = "프로필 이미지 등록")
    @PostMapping("/profile/image/{msrl}")
    public SingleResult<ProfileImg> upload(@PathVariable long msrl, @RequestPart MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Member logined = repository.findByUid(id).orElseThrow(CUserNotFoundException::new);
        if(msrl != logined.getMsrl()) return null;
        if(profileImgRepository.findByMsrl(msrl).isPresent()) { // 기존 프로필 이미지 존재시 삭제 먼저하기
            s3Service.delete(profileImgRepository.findByMsrl(msrl).get().getFilePath());
            profileImgRepository.deleteByMsrl(logined.getMsrl());
        }
        String imgPath = s3Service.upload(file, logined.getMsrl(), 999, logined.getNickname());
        ProfileImgDto profileImgDto = new ProfileImgDto();
        profileImgDto.setFilePath(imgPath);
        profileImgDto.setMsrl(logined.getMsrl());
        return responseService.getSingleResult(profileImgService.savePost(profileImgDto));
    }

    @Transactional
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "회원번호로 회원정보를 삭제한다")
    @DeleteMapping(value = "/delete/{msrl}")
    public CommonResult delete(@ApiParam(value = "회원번호", required = true) @PathVariable long msrl) {
        if(profileImgRepository.findByMsrl(msrl).isPresent()) { // 기존 프로필 이미지 존재시 삭제 먼저하기
            s3Service.delete(profileImgRepository.findByMsrl(msrl).get().getFilePath());
            profileImgRepository.deleteByMsrl(msrl);
        }
//        boardService.deletePosts(msrl);
//        boardService.deleteBoards(msrl);
        repository.deleteById(msrl);
        return responseService.getSuccessResult();
    }
}
