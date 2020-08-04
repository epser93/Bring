package com.web.blog.QnA.service;

import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.OnlyReplyMapping;
import com.web.blog.Common.advice.exception.*;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.model.OnlyApostMapping;
import com.web.blog.QnA.model.OnlyQpostMapping;
import com.web.blog.QnA.model.ParamApost;
import com.web.blog.QnA.model.ParamQpost;
import com.web.blog.QnA.repository.ApostMemberRepository;
import com.web.blog.QnA.repository.ApostRepository;
import com.web.blog.QnA.repository.QpostRepository;
import lombok.RequiredArgsConstructor;
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
    private final FileService fileService;
    private final ApostMemberRepository apostMemberRepository;
    private final ApostRepository apostRepository;
    private final QpostRepository qpostRepository;

    //모든 질문글 리스트
    public List<Qpost> getAllQuestions() {
        return qpostRepository.findAll();
    }

    //한 유저의 모든 답변글 리스트
    public List<Apost> getOnesAllAnswer(Member member) {
        return apostRepository.findByWriter(member.getNickname());
    }

    //모든 질문글 검색
    public List<Qpost> QuestionSearch(int which, long board_id, String keyword) { //검색: which = 1~4
        if (which == 1) { //제목 검색
            return qpostRepository.findBySubjectContaining(keyword);
        } else if (which == 2) { //내용 검색
            return qpostRepository.findByContentContaining(keyword);
        } else if (which == 3) { //작성자 검색
            return qpostRepository.findByWriterContaining(keyword);
        } else { //통합검색
            return qpostRepository.findDistinctBySubjectContainingOrContentContainingOrWriterContaining(keyword, keyword, keyword);
        }
    }

    //질문 상세조회
    public List<OnlyQpostMapping> getOneQpost(long qpost_id) {
        qpostRepository.updateViewCnt(qpost_id);
        return qpostRepository.findByQpostId(qpost_id);
    }

    //질문 작성
    public Qpost writeQuestion(Member member, ParamQpost paramQpost, MultipartFile[] files) throws IOException {
        if (files != null) {
            fileService.uploadFiles(files);
        }
        Qpost qpost = Qpost.builder()
                .writer(member.getNickname())
                .subject(paramQpost.getSubject())
                .content(paramQpost.getContent())
                .build();
        qpostRepository.save(qpost);
        qpostRepository.updateMsrl(member.getMsrl(), qpost.getQpostId());
        return qpost;
    }

    //질문 수정
    public Qpost updateQuestion(Member member, long qpost_id, ParamQpost paramQpost, MultipartFile[] files) throws IOException {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        if (!member.getMsrl().equals(qpost.getMember().getMsrl())) return null;
        if (files != null) {
            fileService.uploadFiles(files);
        }
        qpost.setUpdate(paramQpost.getSubject(), paramQpost.getContent());
        return qpost;
    }

    //질문 삭제
    public boolean deleteQuestion(long qpost_id, Member member, boolean isAnswered) {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        if (!isAnswered && member.getMsrl().equals(qpost.getMember().getMsrl())) {
            qpostRepository.delete(qpost);
            return true;
        } else if (isAnswered) throw new CAnsweredQuestionException();
        else if (!member.getMsrl().equals(qpost.getMember().getMsrl())) throw new CNotOwnerException();
        return false;
    }

    //답변 상세조회
    public List<OnlyApostMapping> getOneApost(long apost_id) {
        return apostRepository.findByApostId(apost_id);
    }

    //한 포스트의 답변 리스트 조회
    public List<OnlyApostMapping> getApostsofOneQpost(long qpost_id) {
        Qpost qpost = qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new);
        return apostRepository.findByQpost(qpost);
    }

    //답변 작성
    public Apost writeAnswer(Qpost qpost, Member member, ParamApost paramApost, MultipartFile[] files) throws IOException {
        if (files != null) {
            fileService.uploadFiles(files);
        }
        if (!qpost.getWriter().equals(member.getNickname())) {
            Apost apost = Apost.builder()
                    .qpost(qpost)
                    .writer(member.getNickname())
                    .answer(paramApost.getAnswer())
                    .build();
            qpostRepository.updateAnswerCnt(qpost.getQpostId());
            return apostRepository.save(apost);
        } else throw new CAskedQuestionException();
    }

    //답변 수정
    public Apost updateAnswer(long apost_id, ParamApost paramApost, MultipartFile[] files, boolean isSelected) throws IOException {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        if (files != null) {
            fileService.uploadFiles(files);
        }
        if (isSelected) {
            return null;
        }
        apost.setUpdate(paramApost.getAnswer());
        return apost;
    }

    //답변 삭제
    public boolean deleteAnswer(long apost_id, Member member, boolean isSelected) {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        if (!isSelected && apost.getWriter().equals(member.getNickname())) {
            apostRepository.delete(apost);
            return true;
        } else if (isSelected) throw new CAnsweredQuestionException();
        else if (!apost.getWriter().equals(member.getNickname())) throw new CNotOwnerException();

        return false;
    }

    //답변 추천
    public void likeThisAnswer(Member member, Apost apost, Boolean like) { //member는 로그인 한 사용자
        long msrl = member.getMsrl();
        long apost_id = apost.getApostId();
        String writer = apost.getWriter();
        Member answerer = memberRepository.findByNickname(writer).orElseThrow(CUserNotFoundException::new); //answerer은 답변자
        if (like) {
            memberRepository.updateScoreIfLiked(answerer.getMsrl());
            apostRepository.updateLikeCntPlus(apost_id);
            apostMemberRepository.insertLike(msrl, apost_id);
        } else {
            memberRepository.updateScoreIfUnliked(answerer.getMsrl());
            apostRepository.updateLikeCntMinus(apost_id);
            apostMemberRepository.deleteLike(msrl, apost_id);
        }
    }

    //답변 채택
    public boolean selectThisAnswer(long apost_id, long qpost_id) {
        Apost apost = apostRepository.findById(apost_id).orElseThrow(CResourceNotExistException::new);
        String writer = apost.getWriter();
        Member answerer = memberRepository.findByNickname(writer).orElseThrow(CUserNotFoundException::new);
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
