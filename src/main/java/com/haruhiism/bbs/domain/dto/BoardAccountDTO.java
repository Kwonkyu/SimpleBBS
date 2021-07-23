package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.account.RegisterRequestCommand;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class BoardAccountDTO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    public static class PagedAccounts {
        private final int currentPage;
        private final int pages;
        private final List<BoardAccountDTO> accounts;

        public PagedAccounts(Page<BoardAccount> accounts) {
            this.currentPage = accounts.getNumber();
            this.pages = accounts.getTotalPages();
            this.accounts = accounts.map(BoardAccountDTO::new).toList();
        }
    }

    private Long id;
    private String userId;
    private String username;
    private String password;
    private String email;
    private String registeredDate;
    private boolean available;
    private String recoveryQuestion;
    private String recoveryAnswer;
    private Set<ManagerLevel> managerLevels = new HashSet<>();

    public void changeUserId(String userId) { this.userId = userId; }

    public void changeUsername(String username) { this.username = username; }

    public void changeEmail(String email) { this.email = email; }

    public void setAvailable() { this.available = true; }
    public void setUnavailable() { this.available = false; }

    public void changeRecoveryQuestion(String recoveryQuestion) { this.recoveryQuestion = recoveryQuestion; }
    public void changeRecoveryAnswer(String recoveryAnswer) { this.recoveryAnswer = recoveryAnswer; }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public BoardAccountDTO(BoardAccount boardAccount){
        this.id = boardAccount.getId();
        this.userId = boardAccount.getUserId();
        this.username = boardAccount.getAlias();
        this.email = boardAccount.getEmail();
        this.registeredDate = formatter.format(boardAccount.getCreatedDateTime());
        this.available = boardAccount.isAvailable();
        this.recoveryQuestion = boardAccount.getRecoveryQuestion();
        this.recoveryAnswer = boardAccount.getRecoveryAnswer();
        this.managerLevels = boardAccount.getManagerLevels();
    }

    public BoardAccountDTO(RegisterRequestCommand command) {
        this.userId = command.getUserId();
        this.username = command.getUsername();
        this.password = command.getPassword();
        this.email = command.getEmail();
        this.available = true;
        this.recoveryQuestion = command.getRecoveryQuestion();
        this.recoveryAnswer = command.getRecoveryAnswer();
    }
}
