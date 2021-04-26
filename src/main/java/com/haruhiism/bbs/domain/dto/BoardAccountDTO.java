package com.haruhiism.bbs.domain.dto;

import com.haruhiism.bbs.command.account.RegisterRequestCommand;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BoardAccountDTO {

    private String userId;
    private String username;
    private String rawPassword;
    private String email;

    public BoardAccountDTO(BoardAccount boardAccount){
        this.userId = boardAccount.getUserId();
        this.username = boardAccount.getUsername();
        this.email = boardAccount.getEmail();
    }

    public BoardAccountDTO(RegisterRequestCommand command) {
        this.userId = command.getUserid();
        this.username = command.getUsername();
        this.rawPassword = command.getPassword();
        this.email = command.getEmail();
    }

    public BoardAccountDTO(LoginSessionInfo loginSessionInfo){
        this.userId = loginSessionInfo.getUserID();
        this.username = loginSessionInfo.getUsername();
        this.email = loginSessionInfo.getEmail();
    }
}
