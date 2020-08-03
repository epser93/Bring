package com.web.blog.QnA.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.Board.entity.CommonDateEntity;
import com.web.blog.Member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@org.hibernate.annotations.DynamicUpdate
public class Qpost extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qpost_id;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 1000)
    private String tag;

    private int views;

    private int answerCnt; //답변 수

    private boolean selectOver;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msrl")
    private Member member;

    public Qpost setUpdate(String subject, String content, String tag) {
        this.subject = subject;
        this.content = content;
        this.tag = tag;
        return this;
    }
}
