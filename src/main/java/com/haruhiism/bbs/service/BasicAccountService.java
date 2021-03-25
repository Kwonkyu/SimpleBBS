package com.haruhiism.bbs.service;

import com.haruhiism.bbs.command.account.UpdatableInformation;
import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.domain.entity.BoardArticle;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.service.authentication.LoginSessionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final ArticleService articleService;
    private final DataEncoder dataEncoder;


    @Override
    @Transactional
    public void registerAccount(BoardAccount boardAccount, AccountLevel level) {
        boardAccount.setPassword(dataEncoder.encode(boardAccount.getPassword()));
        accountRepository.save(boardAccount);
        accountLevelRepository.save(new BoardAccountLevel(boardAccount.getAccountID(), level));
    }

    @Override
    public void withdrawAccount(BoardAccount boardAccount) {
        accountLevelRepository.deleteAllByAccountID(boardAccount.getAccountID());
        accountRepository.delete(boardAccount);
    }

    @Override
    public boolean isDuplicatedAccountByID(String userID) {
        return accountRepository.existsByUserID(userID);
    }

    @Override
    public Page<BoardArticle> readArticlesOfAccount(String userID, Long page) {
        return articleService.readAllByWriterByPages(userID, 0, 10);
    }


    private LoginSessionInfo accountToLoginSessionInfo(BoardAccount account){
        return new LoginSessionInfo(
                account.getAccountID(),
                account.getUserID(),
                account.getUsername(),
                account.getPassword(),
                account.getEmail(),
                accountLevelRepository.findAllByAccountID(account.getAccountID()));
    }

    @Override
    public BoardAccount authenticateAccount(String id, String password) {
        BoardAccount account = accountRepository.findByUserID(id)
                .orElseThrow(NoAccountFoundException::new);

        if(!dataEncoder.compare(password, account.getPassword())){
            throw new AuthenticationFailedException();
        }

        return account;
    }

    @Override
    public LoginSessionInfo loginAccount(String id, String password) {
        return accountToLoginSessionInfo(authenticateAccount(id, password));
    }


    @Override
    @Transactional
    public LoginSessionInfo updateAccount(String id, String password, UpdatableInformation updatedField, String updatedValue) {
        BoardAccount account = authenticateAccount(id, password);
        switch(updatedField){
            case username:
                account.setUsername(updatedValue);
                break;

            case email:
                account.setEmail(updatedValue);
                break;

            case password:
                account.setPassword(dataEncoder.encode(updatedValue));
                break;
        }

        return accountToLoginSessionInfo(account);
    }
}
