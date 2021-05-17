package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountLevelDTO;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;


public interface AccountService {

    public void registerAccount(BoardAccountDTO boardAccountDTO, AccountLevel level);

    public void withdrawAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws AuthenticationFailedException;

    public boolean isDuplicatedUserID(String userId);

    public LoginSessionInfo loginAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws AuthenticationFailedException;

    public LoginSessionInfo updateAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO, UpdatableInformation updatedField, String updatedValue) throws AuthenticationFailedException;

    public BoardAccountLevelDTO getAccountLevels(BoardAccountDTO boardAccountDTO) throws NoAccountFoundException;
}
