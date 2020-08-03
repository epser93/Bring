package com.web.blog.Board.entity;

import com.web.blog.Member.entity.Member;
import lombok.*;

import javax.persistence.*;
//import java.io.Serializable;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends CommonDateEntity { //implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msrl")
    private Member member;

    public Board setUpdate(String name) {
        this.name = name;
        return this;
    }
}
