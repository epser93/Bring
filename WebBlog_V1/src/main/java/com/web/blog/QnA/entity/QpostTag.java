package com.web.blog.QnA.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.web.blog.Board.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class QpostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne
    @JoinColumn(name = "qpost_id")
    private Qpost qpost;

    private int inWhere; //1 = post, 2 = qpost
}
