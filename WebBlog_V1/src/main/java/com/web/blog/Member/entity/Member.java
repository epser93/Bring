package com.web.blog.Member.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.blog.Board.entity.CommonDateEntity;
import com.web.blog.Board.entity.PostMember;
import com.web.blog.Board.entity.ReplyMember;
import com.web.blog.Common.entity.UploadFile;
import com.web.blog.QnA.entity.ApostMember;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//@Proxy(lazy = false)
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(
        name = "userinfo",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"uid", "nickname"}
                )
        })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Member extends CommonDateEntity implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long msrl;

    @Column(nullable = false, length = 100)
    private String uid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Column(length = 100)
    private String provider;

    @Column(columnDefinition = "integer default 0")
    private int likedpost;

    @Column(columnDefinition = "integer default 0")
    private int score;

    @Column(columnDefinition = "integer default 0")
    private int followersCnt;

    @Column(columnDefinition = "integer default 0")
    private int followingCnt;

    private int todayCnt;

    private int totalCnt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filename")
    private UploadFile uploadfile;

    @OneToMany(mappedBy = "member")
    private List<PostMember> postMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ApostMember> apostMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ReplyMember> replyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "from")
    private List<Follow> following;

    @OneToMany(mappedBy = "to")
    private List<Follow> followers;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.uid;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
