package com.haruhiism.bbs.domain.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
// TODO: @Table 어노테이션 및 컬럼 제약조건 추가. 다른 엔티티도 마찬가지.
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
    private Boolean available;

    @NonNull
    @Column(name = "RECOVERY_QUESTION")
    private String recoveryQuestion;

    @NonNull
    @Column(name = "RECOVERY_ANSWER")
    private String recoveryAnswer;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "BOARD_ACCOUNT_CHALLENGE_ID")
    private BoardAccountChallenge challenge = new BoardAccountChallenge(LocalDateTime.now());


    public void registerChallenge(BoardAccountChallenge challenge){
        this.challenge = challenge;
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changePassword(String encodedPassword){
        this.password = encodedPassword;
    }

    public void changeEmail(String email){
        this.email = email;
    }

    public void changeRestoreQuestion(String newQuestion){
        this.recoveryQuestion = newQuestion;
    }

    public void changeRestoreAnswer(String newAnswer){
        this.recoveryAnswer = newAnswer;
    }

    public void invalidate(){
        available = false;
    }

    public void restore(){
        available = true;
    }
}
