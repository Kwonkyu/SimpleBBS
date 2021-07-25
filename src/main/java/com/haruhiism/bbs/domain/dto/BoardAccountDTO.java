package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
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

    private long id;
    private String userId;
    private String username;
    private String password;
    private String email;
    private String registeredDate;
    private boolean available;
    private String recoveryQuestion;
    private String recoveryAnswer;
    private Set<ManagerLevel> managerLevels = new HashSet<>();

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

    public BoardAccountDTO(Register command) {
        this.userId = command.getUserId();
        this.username = command.getUsername();
        this.password = command.getPassword();
        this.email = command.getEmail();
        this.available = true;
        this.recoveryQuestion = command.getRecoveryQuestion();
        this.recoveryAnswer = command.getRecoveryAnswer();
    }

    public enum UpdatableInformation {
        username, email, password, question, answer
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Register {
        @NotBlank(message = "ID cannot be empty.")
        private String userId;

        @NotBlank(message = "Name cannot be empty.")
        private String username;

        @NotBlank(message = "Password cannot be empty.")
        @Length(min = 4, message = "Password should be at least 4 characters.")
        private String password;

        @NotBlank(message = "Email cannot be empty.")
        @Email(message = "Email format should be valid.")
        private String email;

        @NotBlank(message = "Recovery question string cannot be empty.")
        private String recoveryQuestion;

        @NotBlank(message = "Recovery answer string cannot be empty.")
        private String recoveryAnswer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Recovery {
        @NotBlank(message = "User ID cannot be empty.", groups = {Request.class, Submit.class})
        private String userId = "";

        private String question;

        @NotBlank(message = "Answer cannot be empty.", groups = {Submit.class})
        private String answer = "";

        @NotBlank(message = "New password cannot be empty.", groups = {Submit.class})
        private String newPassword = "";

        public interface Request {}

        public interface Submit {}
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Update {
        @NotNull(message = "Pick at least one information to change", groups = {Request.class, Submit.class})
        private BoardAccountDTO.UpdatableInformation mode;

        @NotBlank(message = "Authentication string should not be empty", groups = {Submit.class})
        private String auth = "";

        private String previous = "";

        @NotBlank(message = "Updated string should not be empty", groups = {Submit.class})
        private String updated = "";

        public interface Request {}

        public interface Submit {}
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Login {
        @NotBlank(message = "ID cannot be empty.")
        private String userId = "";

        @NotBlank(message = "Password cannot be empty.")
        private String password = "";
    }

    public enum AccountSearchMode {
        USERID, USERNAME, EMAIL
    }
}
