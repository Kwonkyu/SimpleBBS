package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountLevelDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountChallenge;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.exception.account.AccountChallengeThresholdLimitExceededException;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.repository.AccountChallengeRepository;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final AccountChallengeRepository accountChallengeRepository;
    private final DataEncoder dataEncoder;


    @Override
    public BoardAccountDTO readAccount(BoardAccountDTO boardAccountDTO) {
        return new BoardAccountDTO(accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new));
    }

    @Override
    public void registerAccount(BoardAccountDTO boardAccountDTO) {
        BoardAccount boardAccount = new BoardAccount(
                boardAccountDTO.getUserId(),
                boardAccountDTO.getUsername(),
                dataEncoder.encode(boardAccountDTO.getRawPassword()),
                boardAccountDTO.getEmail(),
                true,
                boardAccountDTO.getRecoveryQuestion(),
                boardAccountDTO.getRecoveryAnswer());
        accountRepository.save(boardAccount);
    }

    @Override
    public void withdrawAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) throws NoAccountFoundException {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new);
        if(authenticateAccount(boardAccount, authDTO)) boardAccount.invalidate();
        else throw new AuthenticationFailedException();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedUserID(String userID) {
        return accountRepository.existsByUserId(userID);
    }

    @Transactional(readOnly = true)
    public boolean authenticateAccount(BoardAccount boardAccount, AuthDTO authDTO) throws NoAccountFoundException, AuthenticationFailedException {
        if(dataEncoder.compare(authDTO.getRawPassword(), boardAccount.getPassword()) ||
                boardAccount.getRecoveryAnswer().equals(authDTO.getRecoveryAnswer())){
            
            boardAccount.getChallenge().clear();
            return true;
        } else {
            return false;
        }
    }


    private boolean challengeAccount(BoardAccount boardAccount){
        // old account has null challenge object.
        if(boardAccount.getChallenge() == null){
            BoardAccountChallenge challenge = new BoardAccountChallenge(LocalDateTime.now());
            accountChallengeRepository.save(challenge);
            boardAccount.registerChallenge(challenge);
        }

        return boardAccount.getChallenge().challenge();
    }

    @Override
    @Transactional(noRollbackFor = {AccountChallengeThresholdLimitExceededException.class, AuthenticationFailedException.class})
    public BoardAccountDTO loginAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new);

        if(!challengeAccount(boardAccount)) {
            throw new AccountChallengeThresholdLimitExceededException(LocalDateTime.now().plusHours(1));
        }

        if(!authenticateAccount(boardAccount, authDTO)){
            throw new AuthenticationFailedException();
        }

        return new BoardAccountDTO(boardAccount);
    }


    @Override
    @Transactional(noRollbackFor = {AccountChallengeThresholdLimitExceededException.class, AuthenticationFailedException.class})
    public BoardAccountDTO updateAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO, UpdatableInformation updatedField, String updatedValue) {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new);

        if(!challengeAccount(boardAccount)) {
            throw new AccountChallengeThresholdLimitExceededException(LocalDateTime.now().plusHours(1));
        }

        if(!authenticateAccount(boardAccount, authDTO)) {
            throw new AuthenticationFailedException();
        }

        switch(updatedField){
            case username:
                boardAccount.changeUsername(updatedValue);
                break;

            case email:
                boardAccount.changeEmail(updatedValue);
                break;

            case password:
                boardAccount.changePassword(dataEncoder.encode(updatedValue));
                break;

            case question:
                boardAccount.changeRestoreQuestion(updatedValue);
                break;

            case answer:
                boardAccount.changeRestoreAnswer(updatedValue);
                break;
        }

        return new BoardAccountDTO(boardAccount);
    }

    @Override
    public BoardAccountLevelDTO getAccountLevels(BoardAccountDTO boardAccountDTO) throws NoAccountFoundException {
        List<ManagerLevel> userLevels = accountLevelRepository.findAllByBoardAccount(
                accountRepository.findByUserIdAndAvailableTrue(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new))
                .stream().map(BoardAccountLevel::getAccountLevel).collect(Collectors.toList());

        return BoardAccountLevelDTO.builder()
                .userId(boardAccountDTO.getUserId())
                .levels(userLevels).build();
    }
}
