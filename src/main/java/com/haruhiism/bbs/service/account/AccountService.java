package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;

public interface AccountService {

    public void registerAccount(BoardAccount boardAccount, AccountLevel level);

    public void withdrawAccount(BoardAccount boardAccount);

    public boolean isDuplicatedAccountByID(String id);

    public BoardAccount authenticateAccount(String id, String password);

    public LoginSessionInfo loginAccount(String id, String password);
}
