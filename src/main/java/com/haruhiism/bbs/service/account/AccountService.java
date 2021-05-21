package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountLevelDTO;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;


public interface AccountService {

    BoardAccountDTO readAccount(BoardAccountDTO boardAccountDTO);

    void registerAccount(BoardAccountDTO boardAccountDTO);

    void withdrawAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws AuthenticationFailedException;

    boolean isDuplicatedUserID(String userId);

    LoginSessionInfo loginAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws AuthenticationFailedException;

    LoginSessionInfo updateAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO, UpdatableInformation updatedField, String updatedValue) throws AuthenticationFailedException;

    BoardAccountLevelDTO getAccountLevels(BoardAccountDTO boardAccountDTO) throws NoAccountFoundException;
}
