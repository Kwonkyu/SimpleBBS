package com.haruhiism.bbs.exception.account;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AccountChallengeThresholdLimitExceededException extends RuntimeException{
    public String errorTitle = "Account Challenge Threshold Limit Exceeded";
    public String errorDescription = "Account login attempt is blocked due to spamming. Please try later.";

    public final LocalDateTime availableTime;
}
