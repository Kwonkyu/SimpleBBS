package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;


public interface AccountService {

    public void registerAccount(BoardAccountDTO boardAccountDTO, AccountLevel level);

    public void withdrawAccount(BoardAccount boardAccount);

    public boolean isDuplicatedAccountByID(String userID);

    public BoardArticlesDTO readArticlesOfAccount(String userID, int page);

    // TODO: fix this shit.
    public BoardAccount authenticateAccount(String id, String password);

    public LoginSessionInfo loginAccount(String id, String password);

    public LoginSessionInfo updateAccount(String id, String password, UpdatableInformation updatedField, String updatedValue);
}
