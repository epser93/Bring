package com.web.blog.QnA.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.Board.entity.CommonDateEntity;
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
public class Apost extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apost_id;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    @Column(columnDefinition = "integer default 0")
    private int likes;

    @Column(columnDefinition = "boolean default false")
    private boolean selected;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qpost_id")
    private Qpost qpost;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "apost", fetch = FetchType.EAGER)
    private List<ApostMember> apostMembers = new ArrayList<>();

    public Apost setUpdate(String answer) {
        this.answer = answer;
        return this;
    }
}
