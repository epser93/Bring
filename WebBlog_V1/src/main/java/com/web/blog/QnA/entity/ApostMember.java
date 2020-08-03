package com.web.blog.QnA.entity;

import com.web.blog.Member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ApostMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "msrl")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "apost_id")
    private Apost apost;
}
