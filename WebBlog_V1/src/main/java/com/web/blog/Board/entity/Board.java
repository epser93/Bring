package com.web.blog.Board.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.web.blog.Member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board extends CommonDateEntity implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 100)
    private String name;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})     //No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer //아마 LAZY 로딩으로 인한 오류같음. hibernateLazyInitializer로 수정.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msrl")
    private Member member;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> posts;

    @Column(columnDefinition = "integer default 0")
    private int postCnt;

    public Board setUpdate(String name) {
        this.name = name;
        return this;
    }
}