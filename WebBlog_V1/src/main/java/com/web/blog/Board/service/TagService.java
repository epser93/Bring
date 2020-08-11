package com.web.blog.Board.service;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostTag;
import com.web.blog.Board.entity.Tag;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.OnlyTagMapping;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostTagRepository;
import com.web.blog.Board.repository.TagRepository;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.response.ListResult;
import com.web.blog.Common.service.ResponseService;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.repository.MemberRepository;
import com.web.blog.QnA.entity.QpostTag;
import com.web.blog.QnA.model.OnlyQpostMapping;
import com.web.blog.QnA.repository.QpostRepository;
import com.web.blog.QnA.repository.QpostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final QpostTagRepository qpostTagRepository;
    private final QpostRepository qpostRepository;
    private final ResponseService responseService;

    //전체 태그 리스트
    public List<OnlyTagMapping> getAllTags() {
        return tagRepository.findAllByOrderByTagUsageCntAsc();
    }

    //msrl 별 태그리스트
    public List<ListResult> getOnesTags(long msrl) {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        List<OnlyPostMapping> listp = postRepository.findAllByMember_Nickname(member.getNickname()); //포스트
        List<OnlyQpostMapping> listq = qpostRepository.findByMember_Nickname(member.getNickname()); //질문글
        List<PostTag> pt = new ArrayList<>();
        List<QpostTag> qt = new ArrayList<>();
        List<String> tagsS = new ArrayList<>(); //태그 목록
        List<Integer> tagsC = new ArrayList<>(); //태그 카운트 목록
        List<OnlyTagMapping> tags = new ArrayList<>();

        //포스트의 태그들
        for(OnlyPostMapping opm : listp) {
            long postId = opm.getPostId();
            pt = postTagRepository.findByPost_PostId(postId);
            for(PostTag tag : pt) {
                Tag t = tag.getTag();
                if(tagsS.contains(t.getTag())) { //이미 존재하면 카운팅 + 1
                    int idx = tagsS.indexOf(t.getTag());
                    tagsC.add(idx, tagsC.get(idx)+1);
                    tagsC.remove(idx+1);
                } else {
                    tagsS.add(t.getTag());
                    tagsC.add(1);
                }
            }
        }

        //질문들의 태그들
        for(OnlyQpostMapping oqm : listq) {
            long qpostId = oqm.getQpostId();
            qt = qpostTagRepository.findByQpost_QpostId(qpostId);
            for(QpostTag tag : qt) {
                Tag t = tag.getTag();
                if(tagsS.contains(t.getTag())) { //이미 존재하면 카운팅 + 1
                    int idx = tagsS.indexOf(t.getTag());
                    tagsC.add(idx, tagsC.get(idx)+1);
                    tagsC.remove(idx+1);
                } else {
                    tagsS.add(t.getTag());
                    tagsC.add(1);
                }
            }
        }

        List<ListResult> result = new ArrayList<>();
        result.add(responseService.getListResult(tagsS));
        result.add(responseService.getListResult(tagsC));
        return result;
    }

    //한 포스트의 태그 리스트 조회
    public List<String> getTags(long post_id) {
        List<PostTag> list = postTagRepository.findByPost(postRepository.findById(post_id).orElseThrow(CResourceNotExistException::new));
        List<String> tags = new ArrayList<>();
        for (PostTag pt : list) {
            Tag tag = pt.getTag();
            tags.add(tag.getTag());
        }
        return tags;
    }

    //포스트 태그 작성
    public Tag insertTags(Post post, String paramTag) {
        Optional<Tag> check = tagRepository.findByTag(paramTag);
        Tag tag = null;
        if (!check.isPresent()) {
            tag = Tag.builder()
                    .tag(paramTag)
                    .build();
            tagRepository.save(tag);
            tagRepository.updateTagUsageCntPlus(tag.getTagId());
            postTagRepository.insertTag(post.getPostId(), tag.getTagId());
        } else {
            tagRepository.updateTagUsageCntPlus(check.get().getTagId());
            postTagRepository.insertTag(post.getPostId(), check.get().getTagId());
        }
        return tag;
    }

    //포스트 태그 수정
    public Tag updateTag(Post post, String paramTag) {
        deleteTags(post);
        return insertTags(post, paramTag); //다시 입력된 값으로 insert
    }

    //태그 전체 삭제
    public void deleteTags(Post post) {
        List<PostTag> originalPostTags = postTagRepository.findByPost(post);
        for (PostTag pt : originalPostTags) {
            Optional<Tag> tag = tagRepository.findByTag(pt.getTag().getTag()); //tag명으로 각 태그들을 찾고
            Tag t = tag.get(); //t에 대입
            if (t.getTagUsageCnt() > 1)
                tagRepository.updateTagUsageCntMinus(t.getTagId()); //해당 태그가 1번 초과 쓰였으면 사용한 내용만 -1
            else if (t.getTagUsageCnt() == 1) { //해당 태그가 한 번밖에 쓰지 않았으면 그냥 태그 통째로 삭제
                tagRepository.delete(t);
            }
            postTagRepository.deleteById(pt.getId()); //post_tag 에서 연결 해제
        }
    }
}
