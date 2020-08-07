package com.web.blog.QnA.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.Board.entity.CommonDateEntity;
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
public class Qpost extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qpostId;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private int views;

    private int answerCnt; //답변 수

    @Column(columnDefinition = "boolean default false")
    private boolean selectOver;

    public void setSelectOver(boolean selectOver) {
        this.selectOver = selectOver;
    }

    public boolean getSelectOver() {
        return this.selectOver;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msrl")
    private Member member;

    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER)
    private List<QpostTag> qpostTags = new ArrayList<>();

    public Qpost setUpdate(String subject, String content) {
        this.subject = subject;
        this.content = content;
        return this;
    }
}
