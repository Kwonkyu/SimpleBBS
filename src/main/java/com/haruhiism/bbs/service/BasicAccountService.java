package com.haruhiism.bbs.service;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasicAccountService implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountLevelRepository accountLevelRepository;

    @Autowired
    private DataEncoder dataEncoder;


    @Override
    @Transactional
    public void registerAccount(BoardAccount boardAccount, AccountLevel level) {
        boardAccount.setPassword(dataEncoder.encode(boardAccount.getPassword()));
        accountRepository.save(boardAccount);
        accountLevelRepository.save(new BoardAccountLevel(boardAccount.getAccountID(), level));
    }

    @Override
    public boolean isDuplicatedAccountByID(String id) {
        return accountRepository.existsByUserid(id);
    }
}
