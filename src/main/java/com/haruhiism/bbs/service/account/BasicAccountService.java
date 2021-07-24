package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;
import com.haruhiism.bbs.repository.AccountLevelRepository;
import com.haruhiism.bbs.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.haruhiism.bbs.service.RepositoryUtility.findAccountByUserId;
import static com.haruhiism.bbs.domain.dto.BoardAccountDTO.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicAccountService implements AccountService, AccountRecoveryService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final AccountLevelRepository accountLevelRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public boolean getChallengeStatus(String userId) throws NoAccountFoundException {
        return findAccountByUserId(accountRepository, userId).challengeStatus();
    }

    @Override
    public boolean authenticateAccount(String userId, String password) {
        BoardAccount boardAccount = findAccountByUserId(accountRepository, userId);
        return passwordEncoder.matches(password, boardAccount.getPassword());
    }

    @Override
    public BoardAccountDTO getAccountInformation(String userId) {
        return new BoardAccountDTO(findAccountByUserId(accountRepository, userId));
    }

    @Override
    public void registerAccount(BoardAccountDTO boardAccountDTO) {
        if (accountRepository.existsByUserId(boardAccountDTO.getUserId())) {
            throw new IllegalArgumentException(String.format("Member %s already exists.", boardAccountDTO.getUserId()));
        }

        boardAccountDTO.encodePassword(passwordEncoder);
        BoardAccount boardAccount = new BoardAccount(
                boardAccountDTO.getUserId(),
                boardAccountDTO.getUsername(),
                boardAccountDTO.getPassword(),
                boardAccountDTO.getEmail(),
                boardAccountDTO.getRecoveryQuestion(),
                boardAccountDTO.getRecoveryAnswer());
        accountRepository.save(boardAccount);
    }

    @Override
    public void withdrawAccount(String userId) throws NoAccountFoundException {
        findAccountByUserId(accountRepository, userId).invalidate();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicatedUserID(String userID) {
        return accountRepository.existsByUserId(userID);
    }


    @Override
    public BoardAccountDTO loginAccount(String userId, String password) {
        throw new NotImplementedException("Login logic delegated to spring security.");
    }


    @Override
    public BoardAccountDTO updateAccount(String userId, UpdatableInformation updatedField, String updatedValue) {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(userId).orElseThrow(NoAccountFoundException::new);

        switch(updatedField){
            case username:
                boardAccount.changeUsername(updatedValue);
                break;

            case email:
                boardAccount.changeEmail(updatedValue);
                break;

            case password:
                boardAccount.changePassword(passwordEncoder.encode(updatedValue));
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
    public boolean challengeAccount(String userId) {
        return findAccountByUserId(accountRepository, userId).challenge();
    }

    @Override
    public void clearAccountChallenges(String userId) throws NoAccountFoundException {
        findAccountByUserId(accountRepository, userId).clearChallenge();
    }

    @Override
    public boolean recoverAccount(String userId, String answer) throws NoAccountFoundException {
        BoardAccount boardAccount = findAccountByUserId(accountRepository, userId);
        return boardAccount.getRecoveryAnswer().equals(answer);
    }

    @Override
    public List<ManagerLevel> getAccountManagerAuthorities(String userId) throws NoAccountFoundException {
        return accountLevelRepository.findAllByBoardAccount(
                findAccountByUserId(accountRepository, userId))
                .stream().map(BoardAccountLevel::getAccountLevel).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BoardAccount boardAccount = accountRepository.findByUserIdAndAvailableTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        boardAccount.challenge();
        return boardAccount;
    }
}
