package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;

public interface AccountService {

    public void registerAccount(BoardAccount boardAccount, AccountLevel level);

    public boolean isDuplicatedAccountByID(String id);

    public LoginSessionInfo authenticateAccount(String id, String password);
}
