package com.web.blog.Common.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.Board.entity.*;
import com.web.blog.Member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@org.hibernate.annotations.DynamicUpdate
public class Feed extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "integer default 0")
    private int views;

    @Column(columnDefinition = "integer default 0")
    private int likes;

    @Column(columnDefinition = "integer default 0")
    private int replyCnt;

    @Column(nullable = true)
    private Long original;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msrl")
    private Member member;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Reply> replies;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostMember> postMembers = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostTag> postTags = new ArrayList<>();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Board getBoard() {
        return board;
    }

    public Feed setUpdate(String subject, String content) {
        this.subject = subject;
        this.content = content;
        return this;
    }
}

