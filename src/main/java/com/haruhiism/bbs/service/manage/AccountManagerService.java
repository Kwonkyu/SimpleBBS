package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.AccountSearchMode;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountManagerService {

    Long countAllAccounts();

    boolean authManagerAccess(String userId);
    void changeManagerLevel(String userId, ManagerLevel level, boolean enable);

    BoardAccountsDTO readAccounts(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);
    BoardAccountsDTO searchAccounts(AccountSearchMode mode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    void invalidateAccounts(List<Long> accountIds);
    void restoreAccounts(List<Long> accountIds);

    void changePassword(List<Long> accountIds, String newPassword);
    void changeUsername(List<Long> accountIds, String newUsername);
}
