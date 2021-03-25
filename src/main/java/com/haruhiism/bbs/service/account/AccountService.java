package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.command.account.UpdatableInformation;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import org.springframework.data.domain.Page;


public interface AccountService {

    public void registerAccount(BoardAccount boardAccount, AccountLevel level);

    public void withdrawAccount(BoardAccount boardAccount);

    public boolean isDuplicatedAccountByID(String userID);

    public Page<BoardArticle> readArticlesOfAccount(String userID, Long page);

    public BoardAccount authenticateAccount(String id, String password);

    public LoginSessionInfo loginAccount(String id, String password);

    public LoginSessionInfo updateAccount(String id, String password, UpdatableInformation updatedField, String updatedValue);
}
