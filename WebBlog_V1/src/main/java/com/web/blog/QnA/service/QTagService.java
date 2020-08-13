package com.web.blog.QnA.service;

import com.web.blog.Board.entity.Tag;
import com.web.blog.Board.repository.TagRepository;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.entity.QpostTag;
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
public class QTagService {
    private final QpostRepository qpostRepository;
    private final TagRepository tagRepository;
    private final QpostTagRepository qpostTagRepository;

    //한 질문글의 태그 리스트 조회
    public List<String> getTags(long qpost_id) {
        List<QpostTag> list = qpostTagRepository.findByQpost(qpostRepository.findById(qpost_id).orElseThrow(CResourceNotExistException::new));
        List<String> tags = new ArrayList<>();
        for (QpostTag pt : list) {
            Tag tag = pt.getTag();
            tags.add(tag.getTag());
        }
        return tags;
    }

    //질문글 태그 작성
    public Tag insertTags(Qpost qpost, String paramTag) {
        Optional<Tag> check = tagRepository.findByTag(paramTag);
        Tag tag = null;
        if (!check.isPresent()) {
            tag = Tag.builder()
                    .tag(paramTag)
                    .build();
            tagRepository.save(tag);
            tagRepository.updateTagUsageCntPlus(tag.getTagId());
            qpostTagRepository.insertTag(qpost.getQpostId(), tag.getTagId());
        } else {
            tagRepository.updateTagUsageCntPlus(check.get().getTagId());
            qpostTagRepository.insertTag(qpost.getQpostId(), check.get().getTagId());
        }
        return tag;
    }

    //질문글 태그 수정
    public Tag updateTag(Qpost qpost, String paramTag) {
        deleteQtags(qpost);
        return insertTags(qpost, paramTag); //다시 입력된 값으로 insert
    }

    public void deleteQtags(Qpost qpost) {
        List<QpostTag> originalQpostTags = qpostTagRepository.findByQpost(qpost);
        for(QpostTag qt : originalQpostTags) {
            Optional<Tag> tag = tagRepository.findByTag(qt.getTag().getTag());
            Tag t = tag.get();
            if(t.getTagUsageCnt() > 1)
                tagRepository.updateTagUsageCntMinus(t.getTagId()); //해당 태그가 1번 초과 쓰였으면 사용한 내용만 -1
            else if (t.getTagUsageCnt() == 1) { //해당 태그가 한 번밖에 쓰지 않았으면 그냥 태그 통째로 삭제
                tagRepository.delete(t);
            }
            qpostTagRepository.deleteByIdEquals(qt.getId()); //post_tag 에서 연결 해제
        }
    }
}
