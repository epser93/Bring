package com.web.blog.Board.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.QnA.entity.QpostTag;
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
public class Tag extends CommonDateEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String tag;

    @Column(columnDefinition = "integer default 0")
    private int tagUsageCnt;

    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER)
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "tag")
    private List<QpostTag> qpostTags = new ArrayList<>();
}
