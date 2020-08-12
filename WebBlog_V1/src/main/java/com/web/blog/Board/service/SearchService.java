package com.web.blog.Board.service;

import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.repository.*;
import com.web.blog.Common.model.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;

    //게시판 내 포스트 검색
    public List<OnlyPostMapping> CategoryPostSearch(int which, long board_id, String keyword, Paging paging) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByBoard_BoardIdAndSubjectContaining(board_id, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByBoard_BoardIdAndContentContaining(board_id, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctByBoard_BoardIdAndSubjectContainingOrBoard_BoardIdAndContentContaining(board_id, keyword, board_id, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //블로그 내 포스트 검색
    public List<OnlyPostMapping> BlogPostSearch(int which, String writer, String keyword, Paging paging) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByMember_NicknameAndSubjectContaining(writer, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByMember_NicknameAndContentContaining(writer, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctByMember_NicknameAndSubjectContainingOrMember_NicknameAndContentContaining(writer, keyword, writer, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //사이트 내 포스트 검색
    public List<OnlyPostMapping> SitePostSearch(int which, String keyword, Paging paging) { //검색: which = 1~4
        if (which == 1) { //제목 검색
            return postRepository.findBySubjectContaining(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByContentContaining(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 3) { //작성자 검색
            return postRepository.findByMember_NicknameContaining(keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctBySubjectContainingOrContentContainingOrMember_NicknameContaining(keyword, keyword, keyword, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }
}
