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
public class BoardAccount extends MACDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ACCOUNT_ID")
    private Long id;
    @NonNull
    @Column(name = "USER_ID")
    private String userId;
    @NonNull
    @Column(name = "USERNAME")
    private String username;
    @NonNull
    @Column(name = "PASSWORD")
    private String password;
    @NonNull
    @Column(name = "EMAIL")
    private String email;
    @NonNull
    @Column(name = "AVAILABLE")
    private boolean available;


    public void changeUsername(String username){
        this.username = username;
    }

    public void changePassword(String encodedPassword){
        this.password = encodedPassword;
    }

    public void changeEmail(String email){
        this.email = email;
    }

    public void invalidate(){
        available = false;
    }

    public void restore(){
        available = true;
    }
}
