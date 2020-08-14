package com.web.blog.Member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.blog.Board.service.BoardService;
import com.web.blog.Common.advice.exception.CEmailSigninFailedException;
import com.web.blog.Common.advice.exception.CUserExistException;
import com.web.blog.Common.config.security.JwtTokenProvider;
import com.web.blog.Common.response.CommonResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.LoginParam;
import com.web.blog.Member.model.ProfileImgDto;
import com.web.blog.Member.model.SignupParam;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.Member.service.EmailService;
import com.web.blog.Member.service.KakaoService;
import com.web.blog.Member.service.ProfileImgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/sign")
public class SignController {
    private final MemberRepository repository;
    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final BoardService boardService;
    private final KakaoService kakaoService;
    private final S3Service s3Service;
    private final ProfileImgService profileImgService;

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);
    @Autowired
    private final EmailService emailService;

    @ApiOperation(value = "로그인", notes = "이메일을 이용한 로그인")
    @PostMapping("/in")
    public String login(@Valid @RequestBody LoginParam user, HttpServletRequest request) throws JsonProcessingException {
        String id = user.getId();
        String password = user.getPassword();
        Member member = repository.findByUid(id).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CEmailSigninFailedException();
        }
        ObjectMapper mapper = new ObjectMapper();
        String Json = "";
        Json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseService.getMapResult(jwtTokenProvider.createToken(String.valueOf(member.getMsrl()), member.getRoles()), member));
//        HttpSession session = request.getSession();
//        session.setAttribute("member", member);
        return Json;
    }

    @ApiOperation(value = "회원가입", notes = "회원가입")
    @PostMapping("/up")
    public CommonResult register(@Valid @RequestBody SignupParam signupParam) throws IOException {
        String id = signupParam.getUid();
        String password = signupParam.getPassword1();
        String passwordChk = signupParam.getPassword2();
        String name = signupParam.getName();
        String nickname = signupParam.getNickname();

        //패스워드 체크
        if (!password.equals(passwordChk)) {
            return responseService.getFailResult(-1001, "비밀번호가 다릅니다.");
        }

        //ID중복 확인
        Optional<Member> member = repository.findByUid(String.valueOf(id));
        if (member.isPresent()) throw new CUserExistException();

        //닉네임 중복 확인
        Optional<Member> member2 = repository.findByNickname(String.valueOf(nickname));
        if (member2.isPresent()) throw new CUserExistException();

//        UploadFile fileName = fileService.uploadFile(file);
        repository.save(Member.builder()
                .uid(id)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        Optional<Member> newUser = repository.findByNickname(nickname);
        boardService.createBoard(id, "나의 Answers");
        URL url = new URL("https://dp02rmdt3p3bw.cloudfront.net/no_img.jpg");
        BufferedImage img = ImageIO.read(url);
        File file = new File("default_img.jpg");
        ImageIO.write(img, "jpg", file);
        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        try {
            IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        String imgPath = s3Service.upload(multipartFile, newUser.get().getMsrl(),999, newUser.get().getNickname());
        ProfileImgDto profileImgDto = new ProfileImgDto();
        profileImgDto.setFilePath(imgPath);
        profileImgDto.setMsrl(newUser.get().getMsrl());
        profileImgService.savePost(profileImgDto);
        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "logout")
    @PostMapping("/out")
    public void logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
    }


//    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
//    @PostMapping(value = "/in/{provider}")
//    public SingleResult<String> signinByProvider(@ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider, @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken) {
//        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
//        Member member = repository.findByUidAndProvider(String.valueOf(profile.getId()), provider).orElseThrow(CUserNotFoundException::new);
//        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(member.getMsrl()), member.getRoles()));
//    }
//
//    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다.")
//    @PostMapping(value = "/up/{provider}")
//    public CommonResult signupProvider(@ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
//                                       @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken,
//                                       @ApiParam(value = "이름", required = true) @RequestParam String name) {
//
//        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
//        Optional<Member> member = repository.findByUidAndProvider(String.valueOf(profile.getId()), provider);
//        if(member.isPresent())
//            throw new CUserExistException();
//
//        repository.save(Member.builder()
//                .uid(String.valueOf(profile.getId()))
//                .provider(provider)
//                .name(name)
//                .roles(Collections.singletonList("ROLE_USER"))
//                .build());
//        return responseService.getSuccessResult();
//    }
//
//    @PostMapping("/up/email")
//    public String emailSender(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("sender")String sender, @RequestParam("receiver")String receiver) throws UnsupportedEncodingException, MessagingException {
//        Email email = new Email(title, content, sender, receiver);
//        try {
//            emailService.send(email);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        return "Email has sent. Please check your Email.";
//    }


}