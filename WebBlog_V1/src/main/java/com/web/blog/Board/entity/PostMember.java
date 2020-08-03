package com.web.blog.Board.entity;

import com.web.blog.Member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "msrl")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
