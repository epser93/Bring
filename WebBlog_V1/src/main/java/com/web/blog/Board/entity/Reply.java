package com.web.blog.Board.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.QnA.entity.Apost;
import com.web.blog.QnA.entity.ApostMember;
import com.web.blog.QnA.entity.Qpost;
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
public class Reply extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reply_id;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(columnDefinition = "LONGTEXT")
    private String reply;

    @Column(columnDefinition = "integer default 0")
    private int likes;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "reply", fetch = FetchType.EAGER)
    private List<ReplyMember> replyMembers = new ArrayList<>();

    public Reply setUpdate(String reply) {
        this.reply = reply;
        return this;
    }
}
