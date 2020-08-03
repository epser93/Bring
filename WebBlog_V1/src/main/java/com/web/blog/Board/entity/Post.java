package com.web.blog.Board.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@org.hibernate.annotations.DynamicUpdate
public class Post extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 1000)
    private String tag;

    @Column(columnDefinition = "integer default 0")
    private int views;

    @Column(columnDefinition = "integer default 0")
    private int likes;

    @Column(columnDefinition = "integer default 0")
    private int replyCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostMember> postMembers = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Board getBoard() {
        return board;
    }

//    public Post(Board board, String writer, String subject, String content, String tag) {
//        this.board = board;
//        this.writer = writer;
//        this.subject = subject;
//        this.content = content;
//        this.tag = tag;
//    }

    public Post setUpdate(String subject, String content, String tag) {
        this.subject = subject;
        this.content = content;
        this.tag = tag;
        return this;
    }
}
