package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.UpdatableInformation;
import com.haruhiism.bbs.domain.authentication.LoginSessionInfo;
import com.haruhiism.bbs.domain.dto.AuthDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountLevelDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.exception.auth.AuthenticationFailedException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import com.haruhiism.bbs.service.DataEncoder.DataEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final DataEncoder dataEncoder;


    @Override
    public BoardAccountDTO readAccount(BoardAccountDTO boardAccountDTO) {
        return new BoardAccountDTO(accountRepository.findByUserId(boardAccountDTO.getUserId()).orElseThrow(NoAccountFoundException::new));
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
        authenticateAccount(boardAccountDTO.getUserId(), authDTO).invalidate();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedUserID(String userID) {
        return accountRepository.existsByUserId(userID);
    }

    @Transactional(readOnly = true)
    public BoardAccount authenticateAccount(String userId, AuthDTO authDTO) throws NoAccountFoundException, AuthenticationFailedException {
        BoardAccount account = accountRepository.findByUserIdAndAvailableTrue(userId).orElseThrow(NoAccountFoundException::new);

        if(dataEncoder.compare(authDTO.getRawPassword(), account.getPassword()) || account.getRecoveryAnswer().equals(authDTO.getRecoveryAnswer())){
            return account;
        } else {
            throw new AuthenticationFailedException();
        }
    }

    @Override
    public LoginSessionInfo loginAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO) {
        return new LoginSessionInfo(authenticateAccount(boardAccountDTO.getUserId(), authDTO));
    }


    @Override
    public LoginSessionInfo updateAccount(BoardAccountDTO boardAccountDTO, AuthDTO authDTO, UpdatableInformation updatedField, String updatedValue) {
        BoardAccount account = authenticateAccount(boardAccountDTO.getUserId(), authDTO);
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

            case question:
                account.changeRestoreQuestion(updatedValue);
                break;

            case answer:
                account.changeRestoreAnswer(updatedValue);
                break;
        }

        return new LoginSessionInfo(account);
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
