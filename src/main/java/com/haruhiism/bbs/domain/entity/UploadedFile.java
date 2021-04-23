package com.haruhiism.bbs.domain.entity;


import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "UPLOADED_FILE")
public class UploadedFile extends MACDate{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;

    @NonNull @Column(name = "FILENAME")
    private String filename;

    @NonNull @Column(name = "FILENAME_HASH")
    private String hashedFilename;

    @Column(name = "REMOTE_URL")
    private String remoteUrl;

    @NonNull
    @ManyToOne @JoinColumn(name = "BOARD_ARTICLE_ID")
    private BoardArticle boardArticle;


    public void registerRemoteUrl(String remoteUrl){
        this.remoteUrl = remoteUrl;
    }
}
