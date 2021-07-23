package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.ManagerLevel;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BoardAccount extends MACDate implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ACCOUNT_ID")
    private long id;

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

    @Column(name = "AVAILABLE")
    private boolean available = true;

    @NonNull
    @Column(name = "RECOVERY_QUESTION")
    private String recoveryQuestion;

    @NonNull
    @Column(name = "RECOVERY_ANSWER")
    private String recoveryAnswer;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "BOARD_ACCOUNT_CHALLENGE_ID")
    private BoardAccountChallenge challenge = new BoardAccountChallenge(LocalDateTime.now());

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "boardAccount")
    private final Set<BoardAccountLevel> managerLevels = new HashSet<>();


    public long getId() { return id; }
    public String getUserId() { return userId; }
    public String getAlias() { return username; }
    public String getEmail() { return email; }
    public boolean isAvailable() { return available; }
    public String getRecoveryQuestion() { return recoveryQuestion; }
    public String getRecoveryAnswer() { return recoveryAnswer; }
    public Set<ManagerLevel> getManagerLevels() {
        return managerLevels.stream().map(BoardAccountLevel::getAccountLevel).collect(Collectors.toSet());
    }
    @Override
    public String getUsername() { return userId; }
    @Override
    public String getPassword() { return password; }

    public void registerChallenge(BoardAccountChallenge challenge) { this.challenge = challenge; }
    public void changeUsername(String username) { this.username = username; }
    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
    public void changeEmail(String email) { this.email = email; }
    public void changeRestoreQuestion(String newQuestion) { this.recoveryQuestion = newQuestion; }
    public void changeRestoreAnswer(String newAnswer) { this.recoveryAnswer = newAnswer; }

    public void invalidate() { available = false; }
    public void restore() { available = true; }
    public BoardAccountChallenge getChallenge() { return challenge; }
    public boolean challenge() { return this.challenge.challenge(); }
    public void clearChallenge() { this.challenge.clear(); }
    public boolean challengeStatus() { return this.challenge.status(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return managerLevels; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return challenge.status(); }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return available; }
}
