package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.SearchMode;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardArticlesDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import com.haruhiism.bbs.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public void withdrawAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws NoAccountFoundException {
        BoardAccount boardAccount = authenticateAccount(boardAccountDTO.getUserId(), authDTO.getRawPassword());
        accountLevelRepository.deleteAllByBoardAccount(boardAccount);
        accountRepository.delete(boardAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedUserID(String userID) {
        return accountRepository.existsByUserId(userID);
    }

    @Transactional(readOnly = true)
    public BoardAccount authenticateAccount(String userId, String rawPassword) throws NoAccountFoundException, AuthenticationFailedException {
        BoardAccount account = accountRepository.findByUserId(userId).orElseThrow(NoAccountFoundException::new);

        if(dataEncoder.compare(rawPassword, account.getPassword())){
            return account;
        } else {
            throw new AuthenticationFailedException();
        }
    }

    @Override
    public LoginSessionInfo loginAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) {
        return new LoginSessionInfo(authenticateAccount(boardAccountDTO.getUserId(), authDTO.getRawPassword()));
    }


    @Override
    public LoginSessionInfo updateAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO, UpdatableInformation updatedField, String updatedValue) {
        // TODO: use session too?
        BoardAccount account = authenticateAccount(boardAccountDTO.getUserId(), authDTO.getRawPassword());
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

        return new LoginSessionInfo(account);
    }
}
