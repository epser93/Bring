package com.web.blog.QnA.service;

import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.model.Paging;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.entity.QpostUploads;
import com.web.blog.QnA.model.*;
import com.web.blog.QnA.repository.ApostMemberRepository;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.repository.QpostUploadsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {
    private final MemberRepository memberRepository;
    private final ApostMemberRepository apostMemberRepository;
    private final ApostRepository apostRepository;
    private final QpostRepository qpostRepository;
    private final QTagService qTagService;
    private final QpostUploadsService qpostUploadsService;
    private final QpostUploadsRepository qpostUploadsRepository;
    private final S3Service s3Service;
    private final PostRepository postRepository;

    public List<String> saveFiles(long qpostId, String nickname, MultipartFile[] files) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        if (files != null) {
//            if (qpostUploadsRepository.findByQpostId(qpostId).isPresent()) { //질문에 사진이 한장이라도 존재하면~
//                List<QpostUploads> beforeUpdate = qpostUploadsRepository.findAllByQpostId(qpostId);
//                qpostUploadsService.deleteImgs(qpostId); //해당하는 질문의 모든 사진 정보 db에서 삭제
//                for (QpostUploads upload : beforeUpdate) { //해당하는 질문의 모든 사진 s3에서 삭제
//                    s3Service.delete(upload.getFilePath());
//                }
//            }

            int num = 0;
            for (MultipartFile file : files) { //s3에 업로드하고 db에 파일 정보 저장
                String imgPath = s3Service.upload(file, qpostId, num, nickname); //s3에 저장
                QpostUploadsDto qpostUploadsDto = new QpostUploadsDto();
                qpostUploadsDto.setFilePath(imgPath);
                qpostUploadsDto.setQpostId(qpostId);
                qpostUploadsDto.setNum(num);
                qpostUploadsDto.setImgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + imgPath);
                qpostUploadsService.savePost(qpostUploadsDto);
                fileUrls.add(qpostUploadsDto.getImgFullPath());
                num++;
            }
        }
        return fileUrls;
    }

    //모든 질문글 리스트
    public List<Qpost> getAllQuestions() {
        return qpostRepository.findAll();
    }

    //한 유저의 모든 답변글 리스트
    public List<Apost> getOnesAllAnswer(Member member) {
        return apostRepository.findByMember_NicknameOrderByApostIdDesc(member.getNickname());
    }

    //모든 질문글 검색
    public List<OnlyQpostMapping> QuestionSearch(int which, String keyword, Paging paging) { //검색: which = 1~4
        if (which == 1) { //제목 검색
            return qpostRepository.findBySubjectContainingOrderByQpostIdDesc(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return qpostRepository.findByContentContainingOrderByQpostIdDesc(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 3) { //작성자 검색
            return qpostRepository.findByMember_NicknameContainingOrderByQpostIdDesc(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return qpostRepository.findDistinctBySubjectContainingOrContentContainingOrMember_NicknameContainingOrderByQpostIdDesc(keyword, keyword, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //질문 상세조회
    public List<OnlyQpostMapping> getOneQpost(long qpost_id) {
        return qpostRepository.findByQpostId(qpost_id);
    }

    //질문 작성
    @Transactional
    public Qpost writeQuestion(Member member, ParamQpost paramQpost) {
        Qpost qpost = qpostRepository.save(Qpost.builder()
                .member(member)
                .subject(paramQpost.getSubject())
                .content(paramQpost.getContent())
                .build());
        qpostRepository.updateMsrl(member.getMsrl(), qpost.getQpostId());
        return qpost;
    }

    //질문 수정
    public Qpost updateQuestion(Member member, long qpost_id, ParamQpost paramQpost) throws IOException {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        if (!member.getMsrl().equals(qpost.getMember().getMsrl())) return null;
        qpost.setUpdate(paramQpost.getSubject(), paramQpost.getContent());
        return qpost;
    }

    //질문 삭제
    public boolean deleteQuestion(long qpost_id, Member member) {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        if (qpost.getAnswerCnt() > 0) throw new CAnsweredQuestionException();
        if (!member.getMsrl().equals(qpost.getMember().getMsrl())) {
            throw new CNotOwnerException();
        } else {
            if (qpost.getAnswerCnt() == 0) {
                qpostRepository.delete(qpost);
                return true;
            } else if (qpost.getAnswerCnt() > 0) throw new CAnsweredQuestionException();
        }

        if (qpostUploadsRepository.findByQpostId(qpost_id).isPresent()) { //질문에 사진이 한장이라도 존재하면~
            System.out.println(true);
            List<QpostUploads> beforeDelete = qpostUploadsRepository.findAllByQpostId(qpost_id);
            qpostUploadsService.deleteImgs(qpost_id); //해당하는 질문의 모든 사진 정보 db에서 삭제
            for (QpostUploads upload : beforeDelete) { //해당하는 질문의 모든 사진 s3에서 삭제
                s3Service.delete(upload.getFilePath());
            }
        }

        return false;
    }

    //답변 상세조회
    public List<OnlyApostMapping> getOneApost(long apost_id) {
        return apostRepository.findByApostId(apost_id);
    }

    //한 포스트의 답변 리스트 조회
    public List<OnlyApostMapping> getApostsofOneQpost(long qpost_id) {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        return apostRepository.findByQpostOrderByApostIdDesc(qpost);
    }

    //답변 작성
    public Apost writeAnswer(Qpost qpost, Member member, ParamApost paramApost, long postId) throws IOException {
        Apost apost = Apost.builder()
                .qpost(qpost)
                .member(member)
                .answer(paramApost.getAnswer())
                .postId(postId)
                .build();

        qpostRepository.updateAnswerCnt(qpost.getQpostId());
        return apostRepository.save(apost);
    }

    //답변 수정
    public Apost updateAnswer(long apost_id, ParamApost paramApost, boolean isSelected) throws IOException {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        if (isSelected) {
            return null;
        }
        apost.setUpdate(paramApost.getAnswer());
        return apost;
    }

    //답변 삭제
    public boolean deleteAnswer(long apost_id, Member member, boolean isSelected) {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        if (!isSelected && apost.getMember().getNickname().equals(member.getNickname())) {
            apostRepository.delete(apost);
            return true;
        } else if (isSelected) throw new CAnsweredQuestionException();
        else if (!apost.getMember().getNickname().equals(member.getNickname())) throw new CNotOwnerException();

        return false;
    }

    //답변 추천
    public void likeThisAnswer(Member member, Apost apost, Boolean like) { //member는 로그인 한 사용자
        long msrl = member.getMsrl();
        long apost_id = apost.getApostId();
        long post_id = apost.getPostId();
        String writer = apost.getMember().getNickname();
        Member answerer = memberRepository.findByNickname(writer).orElseThrow(CUserNotFoundException::new); //answerer은 답변자
        if (like) {
            memberRepository.updateScoreIfLiked(answerer.getMsrl());
            apostRepository.updateLikeCntPlus(apost_id);
            apostMemberRepository.insertLike(msrl, apost_id);
            postRepository.updateLikeCntPlus(post_id);
        } else {
            memberRepository.updateScoreIfUnliked(answerer.getMsrl());
            apostRepository.updateLikeCntMinus(apost_id);
            apostMemberRepository.deleteLike(msrl, apost_id);
            postRepository.updateLikeCntMinus(post_id);
        }
    }

    //답변 채택
    public boolean selectThisAnswer(long apost_id, long qpost_id, Member member) {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        String writer = apost.getMember().getNickname();
        Member answerer = memberRepository.findByNickname(writer).orElseThrow(CUserNotFoundException::new);
        if (member.equals(answerer)) {
            throw new CAskedQuestionException();
        }
        memberRepository.updateScoreIfSelected(answerer.getMsrl());
        qpostRepository.changeSelectedAnswerExist(qpost_id);
        apostRepository.select(apost_id);
        return true;
    }

    //답변을 추천한 유저 목록
    public List<String> likedMemberList(Apost apost) {
        long apost_id = apost.getApostId();
        List<Long> list = apostMemberRepository.likedMember(apost_id);
        List<String> members = new ArrayList<>();
        for (Long id : list) {
            Member member = memberRepository.findById(id).orElseThrow(CUserNotFoundException::new);
            members.add(member.getNickname());
        }
        return members;
    }

}
