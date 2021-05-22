package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.account.RegisterRequestCommand;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardAccountDTO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String userId;
    private String username;
    private String rawPassword;
    private String email;
    private String registeredDate;
    private Boolean available;
    private String recoveryQuestion;
    private String recoveryAnswer;

    public BoardAccountDTO(BoardAccount boardAccount){
        this.id = boardAccount.getId();
        this.userId = boardAccount.getUserId();
        this.username = boardAccount.getUsername();
        this.email = boardAccount.getEmail();
        this.registeredDate = formatter.format(boardAccount.getCreatedDateTime());
        this.available = boardAccount.isAvailable();
        this.recoveryQuestion = boardAccount.getRecoveryQuestion();
        this.recoveryAnswer = boardAccount.getRecoveryAnswer();
    }

    public BoardAccountDTO(RegisterRequestCommand command) {
        this.userId = command.getUserid();
        this.username = command.getUsername();
        this.rawPassword = command.getPassword();
        this.email = command.getEmail();
        this.available = true;
        this.recoveryQuestion = command.getRecoveryQuestion();
        this.recoveryAnswer = command.getRecoveryAnswer();
    }

    public BoardAccountDTO(LoginSessionInfo loginSessionInfo){
        this.userId = loginSessionInfo.getUserID();
        this.username = loginSessionInfo.getUsername();
        this.email = loginSessionInfo.getEmail();
    }
}
