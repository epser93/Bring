package com.web.blog.Board.service;

import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostTag;
import com.web.blog.Board.entity.Tag;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostTagRepository;
import com.web.blog.Board.repository.TagRepository;
import com.web.blog.Common.model.Paging;
import com.web.blog.QnA.entity.Qpost;
import com.web.blog.QnA.entity.QpostTag;
import com.web.blog.QnA.model.OnlyQpostMapping;
import com.web.blog.QnA.repository.QpostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final QpostTagRepository qpostTagRepository;
    private final TagRepository tagRepository;

    //게시판 내 포스트 검색
    public List<OnlyPostMapping> CategoryPostSearch(int which, long board_id, String keyword, Paging paging) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByBoard_BoardIdAndSubjectContainingAndBoard_NameNotLike(board_id, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByBoard_BoardIdAndContentContainingAndBoard_NameNotLike(board_id, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctByBoard_BoardIdAndSubjectContainingAndBoard_NameNotLikeOrBoard_BoardIdAndContentContainingAndBoard_NameNotLike(board_id, keyword, "나의 Answers", board_id, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //블로그 내 포스트 검색
    public List<OnlyPostMapping> BlogPostSearch(int which, String writer, String keyword, Paging paging) { //검색: which = 1~3
        if (which == 1) { //제목 검색
            return postRepository.findByMember_NicknameAndSubjectContainingAndBoard_NameNotLike(writer, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByMember_NicknameAndContentContainingAndBoard_NameNotLike(writer, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctByMember_NicknameAndSubjectContainingAndBoard_NameNotLikeOrMember_NicknameAndContentContainingAndBoard_NameNotLike(writer, keyword, "나의 Answers", writer, keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //사이트 내 포스트 검색
    public List<OnlyPostMapping> SitePostSearch(int which, String keyword, Paging paging) { //검색: which = 1~4
        if (which == 1) { //제목 검색
            return postRepository.findBySubjectContainingAndBoard_NameNotLike(keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 2) { //내용 검색
            return postRepository.findByContentContainingAndBoard_NameNotLike(keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else if (which == 3) { //작성자 검색
            return postRepository.findByMember_NicknameContainingAndBoard_NameNotLike(keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        } else { //통합검색
            return postRepository.findDistinctBySubjectContainingAndBoard_NameNotLikeOrContentContainingAndBoard_NameNotLikeOrMember_NicknameContainingAndBoard_NameNotLike(keyword, "나의 Answers", keyword, "나의 Answers", keyword, "나의 Answers", PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        }
    }

    //전체 블로그의 태그 검색
    public List<OnlyPostMapping> AllBlogTagSearch(String keyword, Paging paging) {
        Optional<Tag> tag = tagRepository.findByTag(keyword);
        if(tag.isPresent()) return null;
        List<PostTag> postTags = postTagRepository.findByTagAndInWhereAndAnswersNot(tag.get(), 1, true, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        List<OnlyPostMapping> posts = new ArrayList<>();
        for (PostTag pt : postTags) {
            Post post = pt.getPost();
            OnlyPostMapping onlyPostMapping = new OnlyPostMapping() {
                @Override
                public Long getPostId() {
                    return post.getPostId();
                }

                @Override
                public LocalDateTime getCreatedAt() {
                    return post.getCreatedAt();
                }

                @Override
                public LocalDateTime getUpdatedAt() {
                    return post.getUpdatedAt();
                }

                @Override
                public String getMember_nickname() {
                    return post.getMember().getNickname();
                }

                @Override
                public String getSubject() {
                    return post.getSubject();
                }

                @Override
                public String getContent() {
                    return post.getContent();
                }

                @Override
                public int getViews() {
                    return post.getViews();
                }

                @Override
                public int getLikes() {
                    return post.getLikes();
                }

                @Override
                public int getReplyCnt() {
                    return post.getReplyCnt();
                }

                @Override
                public String getBoard_name() {
                    return post.getBoard().getName();
                }

                @Override
                public Long getOriginal() {
                    return post.getOriginal();
                }

                @Override
                public String getMember_uid() {
                    return post.getMember().getUid();
                }
            };
            posts.add(onlyPostMapping);
        }
        return posts;
    }

    //특정 블로그의 태그 검색
    public List<OnlyPostMapping> OnesBlogTagSearch(String nickname, String keyword, Paging paging) {
        Optional<Tag> tag = tagRepository.findByTag(keyword);
        if(!tag.isPresent()) return null;
        List<PostTag> postTags = postTagRepository.findByTagAndInWhereAndAnswersNot(tag.get(), 1, true,PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        List<OnlyPostMapping> posts = new ArrayList<>();
        for (PostTag pt : postTags) {
            Post post = pt.getPost();
            String member = post.getMember().getNickname();
            if (member.equals(nickname)) {
                OnlyPostMapping onlyPostMapping = new OnlyPostMapping() {
                    @Override
                    public Long getPostId() {
                        return post.getPostId();
                    }

                    @Override
                    public LocalDateTime getCreatedAt() {
                        return post.getCreatedAt();
                    }

                    @Override
                    public LocalDateTime getUpdatedAt() {
                        return post.getUpdatedAt();
                    }

                    @Override
                    public String getMember_nickname() {
                        return post.getMember().getNickname();
                    }

                    @Override
                    public String getSubject() {
                        return post.getSubject();
                    }

                    @Override
                    public String getContent() {
                        return post.getContent();
                    }

                    @Override
                    public int getViews() {
                        return post.getViews();
                    }

                    @Override
                    public int getLikes() {
                        return post.getLikes();
                    }

                    @Override
                    public int getReplyCnt() {
                        return post.getReplyCnt();
                    }

                    @Override
                    public String getBoard_name() {
                        return post.getBoard().getName();
                    }

                    @Override
                    public Long getOriginal() {
                        return post.getOriginal();
                    }

                    @Override
                    public String getMember_uid() {
                        return post.getMember().getUid();
                    }
                };
                posts.add(onlyPostMapping);
            }
        }
        return posts;
    }

    //전체 지식인의 태그 검색
    public List<OnlyQpostMapping> AllQnaTagSearch(String keyword, Paging paging) {
        Optional<Tag> tag = tagRepository.findByTag(keyword);
        if(!tag.isPresent()) return null;
        List<QpostTag> qpostTags = qpostTagRepository.findByTagAndInWhere(tag.get(), 2, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        List<OnlyQpostMapping> qposts = new ArrayList<>();
        for (QpostTag qt : qpostTags) {
            Qpost qpost = qt.getQpost();
            OnlyQpostMapping onlyQpostMapping = new OnlyQpostMapping() {
                @Override
                public Long getQpostId() {
                    return qpost.getQpostId();
                }

                @Override
                public LocalDateTime getCreatedAt() {
                    return qpost.getCreatedAt();
                }

                @Override
                public LocalDateTime getUpdatedAt() {
                    return qpost.getUpdatedAt();
                }

                @Override
                public String getMember_nickname() {
                    return qpost.getMember().getNickname();
                }

                @Override
                public String getSubject() {
                    return qpost.getSubject();
                }

                @Override
                public String getContent() {
                    return qpost.getContent();
                }

                @Override
                public int getViews() {
                    return qpost.getViews();
                }

                @Override
                public int getAnswerCnt() {
                    return qpost.getAnswerCnt();
                }

                @Override
                public boolean getSelectOver() {
                    return qpost.getSelectOver();
                }
            };
            qposts.add(onlyQpostMapping);
        }
        return qposts;
    }

    //특정 유저의 지식인 태그 검색
    public List<OnlyQpostMapping> OnesQnaTagSearch(String nickname, String keyword, Paging paging) {
        Optional<Tag> tag = tagRepository.findByTag(keyword);
        if(!tag.isPresent()) return null;
        List<QpostTag> qpostTags = qpostTagRepository.findByTagAndInWhere(tag.get(), 2, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
        List<OnlyQpostMapping> qposts = new ArrayList<>();
        for (QpostTag qt : qpostTags) {
            Qpost qpost = qt.getQpost();
            String member = qpost.getMember().getNickname();
            if (member.equals(nickname)) {
                OnlyQpostMapping onlyQpostMapping = new OnlyQpostMapping() {
                    @Override
                    public Long getQpostId() {
                        return qpost.getQpostId();
                    }

                    @Override
                    public LocalDateTime getCreatedAt() {
                        return qpost.getCreatedAt();
                    }

                    @Override
                    public LocalDateTime getUpdatedAt() {
                        return qpost.getUpdatedAt();
                    }

                    @Override
                    public String getMember_nickname() {
                        return qpost.getMember().getNickname();
                    }

                    @Override
                    public String getSubject() {
                        return qpost.getSubject();
                    }

                    @Override
                    public String getContent() {
                        return qpost.getContent();
                    }

                    @Override
                    public int getViews() {
                        return qpost.getViews();
                    }

                    @Override
                    public int getAnswerCnt() {
                        return qpost.getAnswerCnt();
                    }

                    @Override
                    public boolean getSelectOver() {
                        return qpost.getSelectOver();
                    }
                };
                qposts.add(onlyQpostMapping);
            }
        }
        return qposts;
    }
}
