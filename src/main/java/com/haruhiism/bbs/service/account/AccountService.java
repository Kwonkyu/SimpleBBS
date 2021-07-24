package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.exception.account.NoAccountFoundException;

import java.util.List;

import static com.haruhiism.bbs.domain.dto.BoardAccountDTO.*;

public interface AccountService {

    /**
     * Authenticate user account with id and password.
     * @param userId User's id.
     * @param password User's password.
     * @return boolean value indicating authenticated or not.
     */
    boolean authenticateAccount(String userId, String password);

    /**
     * Get account's information.
     * @param userId User's id.
     * @return BoardAccountDTO object filled with account's information.
     * @throws NoAccountFoundException when user with given id not found.
     */
    BoardAccountDTO getAccountInformation(String userId) throws NoAccountFoundException;

    /**
     * Register new account.
     * @param boardAccountDTO BoardAccountDTO object filled with registering account's information.
     * @throws IllegalArgumentException when mandatory field is omitted.
     */
    void registerAccount(BoardAccountDTO boardAccountDTO) throws IllegalArgumentException;

    /**
     * Withdraw account.
     * @param userId User's id.
     * @throws NoAccountFoundException when user with given id not found.
     */
    void withdrawAccount(String userId) throws NoAccountFoundException;

    /**
     * Check user with given id already exists.
     * @param userId User's id.
     * @return boolean value indicating id is duplicated or not.
     */
    boolean isDuplicatedUserID(String userId);

    /**
     * Authenticate and get information of account.
     * @param userId User's id.
     * @param password User's password.
     * @return BoardAccountDTO object filled with account information.
     * @throws NoAccountFoundException when user with given id not found.
     */
    BoardAccountDTO loginAccount(String userId, String password) throws NoAccountFoundException;

    /**
     * Update account information.
     * @param userId User's id.
     * @param updatedField Updating field of account.
     * @param updatedValue Updating value of account.
     * @return BoardAccountDTO object filled with updated account.
     * @throws NoAccountFoundException when user with given id not found.
     */
    BoardAccountDTO updateAccount(String userId, UpdatableInformation updatedField, String updatedValue) throws NoAccountFoundException;

    /**
     * Get account's manager authorities.
     * @param userId User's id.
     * @return List of ManagerLevel enums which indicating authority.
     * @throws NoAccountFoundException when user with given id not found.
     */
    List<ManagerLevel> getAccountManagerAuthorities(String userId) throws NoAccountFoundException;
}
