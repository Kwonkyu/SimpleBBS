package com.haruhiism.bbs.service.account;

import com.haruhiism.bbs.exception.account.NoAccountFoundException;

public interface AccountRecoveryService {

    /**
     * Get challenge status of account.
     * @param userId User's id.
     * @return boolean value indicating challenge limit is not exceeded or not.
     * @throws NoAccountFoundException when user with given id not found.
     */
    boolean getChallengeStatus(String userId) throws NoAccountFoundException;

    /**
     * Challenge account.
     * @param userId User's id.
     * @return boolean value indicating account challenge threshold not exceeds limit yet.
     * @throws NoAccountFoundException when user with given id not found.
     */
    boolean challengeAccount(String userId) throws NoAccountFoundException;

    /**
     * Clear account challenge history.
     * @param userId User's id.
     * @throws NoAccountFoundException when user with given id not found.
     */
    void clearAccountChallenges(String userId) throws NoAccountFoundException;

    /**
     * Challenge account with recovery answer.
     * @param userId User's id.
     * @param answer Recovery question's answer.
     * @return boolean value indicating recovery allowed or not.
     * @throws NoAccountFoundException when user with given id not found.
     */
    boolean recoverAccount(String userId, String answer) throws NoAccountFoundException;
}
