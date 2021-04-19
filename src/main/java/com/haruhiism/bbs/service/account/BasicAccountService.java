package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.exception.AuthenticationFailedException;
import com.haruhiism.bbs.exception.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.account.AccountService;
import com.haruhiism.bbs.service.article.ArticleService;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final ArticleService articleService;
    private final DataEncoder dataEncoder;


    @Override
    public void registerAccount(BoardAccountDTO boardAccountDTO, AccountLevel level) {
        BoardAccount boardAccount = new BoardAccount(
                boardAccountDTO.getUserId(),
                boardAccountDTO.getUsername(),
                dataEncoder.encode(boardAccountDTO.getRawPassword()),
                boardAccountDTO.getEmail());
        accountRepository.save(boardAccount);

        accountLevelRepository.save(new BoardAccountLevel(boardAccount, level));
    }

    @Override
    public void withdrawAccount(BoardAccount boardAccount) {
        accountLevelRepository.deleteAllByBoardAccount(boardAccount);
        accountRepository.delete(boardAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedAccountByID(String userID) {
        return accountRepository.existsByUserId(userID);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardArticlesDTO readArticlesOfAccount(String userID, int page) {
        return articleService.searchAllByPages(SearchMode.WRITER, userID, page, 10);
    }


    private LoginSessionInfo updateAccountSession(BoardAccount account){
        return new LoginSessionInfo(account);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardAccount authenticateAccount(String id, String password) {
        BoardAccount account = accountRepository.findByUserId(id)
                .orElseThrow(NoAccountFoundException::new);

        if(!dataEncoder.compare(password, account.getPassword())){
            throw new AuthenticationFailedException();
        }

        return account;
    }

    @Override
    public LoginSessionInfo loginAccount(String id, String password) {
        return updateAccountSession(authenticateAccount(id, password));
    }


    @Override
    public LoginSessionInfo updateAccount(String id, String password, UpdatableInformation updatedField, String updatedValue) {
        BoardAccount account = authenticateAccount(id, password);
        switch(updatedField){
            case username:
                account.changeUsername(updatedValue);
                break;

            case email:
                account.changeEmail(updatedValue);
                break;

            case password:
                account.changePassword(dataEncoder.encode(updatedValue));
                break;
        }

        return updateAccountSession(account);
    }
}
