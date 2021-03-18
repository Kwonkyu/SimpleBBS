package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;

public interface AccountService {

    public void registerAccount(BoardAccount boardAccount, AccountLevel level);

    public boolean isDuplicatedAccountByID(String id);
}
