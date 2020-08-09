//package com.web.blog.Common.entity;
//
//import com.web.blog.Board.entity.Post;
//import com.web.blog.Board.entity.Reply;
//import com.web.blog.QnA.entity.Apost;
//import com.web.blog.QnA.entity.Qpost;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import java.util.Date;
//
//@Entity
//@Getter
//@Setter
//@Data
//@NoArgsConstructor
//@Table(name = "uploadfile")
//public class UploadFile {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Column(name = "filename")
//    private String filename;
//
//    @Column(name = "filesize")
//    private long filesize;
//
//    @Column(name = "filetype")
//    private String filetype;
//
//    @CreationTimestamp
//    @Column(name = "insert_date")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date insertDate;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id")
//    private Post post;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reply_id")
//    private Reply reply;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "qpost_id")
//    private Qpost qpost;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "apost_id")
//    private Apost apost;
//
//    public UploadFile(String filename, long filesize, String filetype) {
//        this.filename = filename;
//        this.filesize = filesize;
//        this.filetype = filetype;
//    }
//
//    public UploadFile(String filename, long filesize, String filetype, Post post) {
//        this.filename = filename;
//        this.filesize = filesize;
//        this.filetype = filetype;
//        this.post = post;
//    }
//}
