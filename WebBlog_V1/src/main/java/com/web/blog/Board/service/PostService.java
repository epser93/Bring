package com.web.blog.Board.service;

import com.web.blog.Board.entity.Board;
import com.web.blog.Board.entity.Post;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.entity.PostUploads;
import com.web.blog.Board.model.OnlyPostMapping;
import com.web.blog.Board.model.ParamPost;
import com.web.blog.Board.model.PostUploadsDto;
import com.web.blog.Board.repository.BoardRepository;
import com.web.blog.Board.repository.PostMemberRepository;
import com.web.blog.Board.repository.PostRepository;
import com.web.blog.Board.repository.PostUploadsRepository;
import com.web.blog.Common.advice.exception.CNotOwnerException;
import com.web.blog.Common.advice.exception.CResourceNotExistException;
import com.web.blog.Common.advice.exception.CUserNotFoundException;
import com.web.blog.Common.service.S3Service;
import com.web.blog.Member.entity.Member;
import com.web.blog.Member.model.OnlyMemberMapping;
import com.web.blog.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostMemberRepository postMemberRepository;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final BoardService boardService;
    private final PostUploadsRepository postUploadsRepository;
    private final PostUploadsService postUploadsService;
    private final S3Service s3Service;

    //게시글 단건 조회
    public List<OnlyPostMapping> getPost(long postId) {
        return postRepository.findByPostId(postId);
    }

    public Post getPost2(long postId) {
        postRepository.updateViewCnt(postId);
        return postRepository.findById(postId).get();
    }

    public List<String> saveFiles(long postId, String nickname, MultipartFile[] files) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        if (files != null) {
            int num = 0;
            for (MultipartFile file : files) { //s3에 업로드하고 db에 파일 정보 저장
                String imgPath = s3Service.upload(file, postId, num, nickname); //s3에 저장
                PostUploadsDto postUploadsDto = new PostUploadsDto();
                postUploadsDto.setFilePath(imgPath);
                postUploadsDto.setFileName(file.getOriginalFilename());//
                postUploadsDto.setNickname(nickname);//
                postUploadsDto.setPostId(postId);
                postUploadsDto.setNum(num);
                postUploadsDto.setImgFullPath("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + imgPath);
                postUploadsService.savePost(postUploadsDto);
                fileUrls.add("https://" + s3Service.CLOUD_FRONT_DOMAIN_NAME + "/" + imgPath);
                num++;
            }
        }
        return fileUrls;
    }

    //게시글 작성
    public Post writePost(String boardName, ParamPost paramPost, Member member, String originWriter) {
        Board board = boardService.findBoard(boardName, member);

        Post result = null;
        if (paramPost.getOriginal() != -1) {
            List<OnlyPostMapping> originalPost = postRepository.findByPostId(paramPost.getOriginal());
            OnlyPostMapping opm = originalPost.get(0);

            StringBuilder sb = new StringBuilder();
            sb.append("[출처]" + originWriter);
            sb.append(" ");
            String subject = paramPost.getSubject();
            sb.append(subject);
            subject = sb.toString();

            sb = new StringBuilder();
            String content = paramPost.getContent();
            sb.append(content);
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));
            sb.append("[Copyright by " + originWriter);
            sb.append("  Category: " + opm.getBoard_name() + "  Post ID: " + paramPost.getOriginal() + "]");
            content = sb.toString();

            result = postRepository.save(Post.builder()
                    .board(board)
                    .member(member)
                    .subject(subject)
                    .content(content)
                    .original(paramPost.getOriginal())
                    .build());
            boardRepository.updatePostCnt(board.getBoardId());
        } else {
            result = postRepository.save(Post.builder()
                    .board(board)
                    .member(member)
                    .subject(paramPost.getSubject())
                    .content(paramPost.getContent())
                    .original(paramPost.getOriginal())
                    .build());
            boardRepository.updatePostCnt(board.getBoardId());
        }
        return result;
    }

    //게시글 좋아요
    public void likePost(Member member, Post post, Boolean like) {
        long msrl = member.getMsrl(); //좋아요를 누른 사람
        long postId = post.getPostId();
        long original = post.getOriginal();
        String nickname = post.getMember().getNickname();
        Member writer = memberRepository.findByNickname(nickname).orElseThrow(CUserNotFoundException::new); //post의 작성자
        List<OnlyPostMapping> originalPost = null;
        if (like) {
            postRepository.updateLikeCntPlus(postId); //해당 포스트 글의 좋아요 업데이트
            if (original != -1) { //포스트의 오리지널이 존재하면
                originalPost = postRepository.findByPostId(original);
                String originWriter = originalPost.get(0).getMember_nickname();
                Member originMember = memberRepository.findByNickname(originWriter).orElseThrow(CUserNotFoundException::new);
                postRepository.updateLikeCntPlus(original); //공유된 포스트 좋아요 업데이트(원래 포스트)
                Post originalPost1 = postRepository.findById(original).orElseThrow(CResourceNotExistException::new);
                Optional<PostMember> postMember = postMemberRepository.findPostMemberByMember_MsrlAndPost(msrl, originalPost1);
                if (!postMember.isPresent()) { //공유된 포스트를 좋아요 누른 적이 없으면~
                    postMemberRepository.insertLike(msrl, original); //공유된 포스트 좋아요 연결 추가
                }
                memberRepository.updateScoreIfLiked(originMember.getMsrl());
            } else { //존재하지 않으면
                memberRepository.updateScoreIfLiked(writer.getMsrl());
            }
            postMemberRepository.insertLike(msrl, postId);
        } else {
            memberRepository.updateScoreIfUnliked(writer.getMsrl());
            postRepository.updateLikeCntMinus(postId);
            if (original != -1) {
                originalPost = postRepository.findByPostId(original);
                String originWriter = originalPost.get(0).getMember_nickname();
                Member originMember = memberRepository.findByNickname(originWriter).orElseThrow(CUserNotFoundException::new);
                postRepository.updateLikeCntMinus(original); //공유된 포스트 좋아요 업데이트(원래 포스트)
                postMemberRepository.deleteLike(msrl, original); //공유된 포스트 좋아요 연결 해제
                memberRepository.updateScoreIfUnliked(originMember.getMsrl());
            }
            postMemberRepository.deleteLike(msrl, postId);
        }
    }

    //유저가 좋아요 한 글 목록
    public List<Post> likedPostList(Member member) {
        long msrl = member.getMsrl();
        List<Long> list = postMemberRepository.likedPost(msrl);
        List<Post> posts = new ArrayList<>();
        for (Long id : list) {
            Post post = postRepository.findById(id).orElseThrow(CResourceNotExistException::new);
            posts.add(post);
        }
        return posts;
    }

    //게시글에 좋아요를 누른 유저 목록
    public List<OnlyMemberMapping> likedMemberListMM(long post_id) {
        List<Long> list = postMemberRepository.likedMember(post_id);
        List<OnlyMemberMapping> members = new ArrayList<>();
        for (Long id : list) {
            Optional<OnlyMemberMapping> member = memberRepository.findByMsrl(id);
            members.add(member.get());
        }
        return members;
    }

    //게시글에 좋아요를 누른 유저 목록
    public List<String> likedMemberList(long post_id) {
        List<Long> list = postMemberRepository.likedMember(post_id);
        List<String> members = new ArrayList<>();
        for (Long id : list) {
            Optional<Member> member = memberRepository.findById(id);
            members.add(member.get().getNickname());
        }
        return members;
    }

    public OnlyMemberMapping getOnlyMember(Member member) {
        Optional<OnlyMemberMapping> mm = memberRepository.findByMsrl(member.getMsrl());
        return mm.get();
    }

    //게시글 수정
    public Post updatePost(String boardname, long postId, long msrl, ParamPost paramPost) throws IOException {
        Member member = memberRepository.findById(msrl).orElseThrow(CUserNotFoundException::new);
        Board board = boardRepository.findByNameAndMember(boardname, member);
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        Board board1 = post.getBoard();
        if (board.equals(board1)) {
            try {
                post.setUpdate(paramPost.getSubject(), paramPost.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return post;
    }

    //모든 좋아요 삭제
    public void deleteLikes(Post post) {
        List<PostMember> likers = postMemberRepository.findPostMemberByPost(post);
        for (PostMember pm : likers) {
            postMemberRepository.delete(pm);
        }
    }

    //게시글 삭제
    public boolean deletePost(long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(CResourceNotExistException::new);
        String writer = post.getMember().getNickname();
        Optional<Member> member2 = memberRepository.findByNickname(writer);
        if (!member2.get().getNickname().equals(member.getNickname())) throw new CNotOwnerException();
        postRepository.delete(post);
        boardRepository.updatePostCntMinus(post.getBoard().getBoardId());
        if (postUploadsRepository.findByPostId(postId).isPresent()) { //포스트에 사진이 한장이라도 존재하면~
            List<PostUploads> beforeDelete = postUploadsRepository.findAllByPostId(postId);
            postUploadsService.deleteImgs(postId); //해당하는 포스트의 모든 사진 정보 db에서 삭제
            for (PostUploads upload : beforeDelete) { //해당하는 포스트의 모든 사진 s3에서 삭제
                s3Service.delete(upload.getFilePath());
            }
        }
        return true;
    }
}
