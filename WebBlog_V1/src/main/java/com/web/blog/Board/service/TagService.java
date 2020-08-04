package com.web.blog.Board.service;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostTag;
import com.web.blog.Board.entity.Tag;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.service.FileService;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

    //전체 태그 리스트
    public List<Tag> getAllTags() {
        return tagRepository.findAll(Sort.by("tag_usage_cnt"));
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
        return insertTags(post, paramTag); //다시 입력된 값으로 insert
    }
}
