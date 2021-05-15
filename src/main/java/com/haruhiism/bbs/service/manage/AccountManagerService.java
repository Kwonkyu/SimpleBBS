package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.AccountLevel;
import com.haruhiism.bbs.domain.AccountSearchMode;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;
import com.haruhiism.bbs.domain.dto.BoardAccountsDTO;

import java.util.List;

public interface AccountManagerService {

    Long countAllAccounts();

    List<AccountLevel> getLevelOfAccount(BoardAccountDTO boardAccountDTO);

    BoardAccountsDTO readAccounts(int pageNum, int pageSize);
    BoardAccountsDTO searchAccounts(AccountSearchMode mode, String keyword, int pageNum, int pageSize);

    void invalidateAccounts(List<Long> accountIds);
    void restoreAccounts(List<Long> accountIds);

    void changePassword(List<Long> accountIds, String newPassword);

    void changeUsername(List<Long> accountIds, String newUsername);
}
