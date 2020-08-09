package com.web.blog.Board.service;

import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;

    //게시판 내 포스트 검색
    public List<OnlyPostMapping> CategoryPostSearch(int which, long board_id, String keyword) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByBoard_BoardIdAndSubjectContaining(board_id, keyword);
        } else if (which == 2) { //내용 검색
            return postRepository.findByBoard_BoardIdAndContentContaining(board_id, keyword);
        } else { //통합검색
            return postRepository.findDistinctByBoard_BoardIdAndSubjectContainingOrBoard_BoardIdAndContentContaining(board_id, keyword, board_id, keyword);
        }
    }

    //블로그 내 포스트 검색
    public List<OnlyPostMapping> BlogPostSearch(int which, String writer, String keyword) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByWriterAndSubjectContaining(writer, keyword);
        } else if (which == 2) { //내용 검색
            return postRepository.findByWriterAndContentContaining(writer, keyword);
        } else { //통합검색
            return postRepository.findDistinctByWriterAndSubjectContainingOrWriterAndContentContaining(writer, keyword, writer, keyword);
        }
    }

    //사이트 내 포스트 검색
    public List<OnlyPostMapping> SitePostSearch(int which, String keyword) { //검색: which = 1~4
        if (which == 1) { //제목 검색
            return postRepository.findBySubjectContaining(keyword);
        } else if (which == 2) { //내용 검색
            return postRepository.findByContentContaining(keyword);
        } else if (which == 3) { //작성자 검색
            return postRepository.findByWriterContaining(keyword);
        } else { //통합검색
            return postRepository.findDistinctBySubjectContainingOrContentContainingOrWriterContaining(keyword, keyword, keyword);
        }
    }
}
