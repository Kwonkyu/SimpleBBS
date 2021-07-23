package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.AccountSearchMode;
import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.dto.BoardAccountDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountManagerService {

    /**
     * Count how many articles written.
     * @return count of articles.
     */
    long countAllAccounts();

    /**
     * Check if user is manager of not.
     * @param userId User's id.
     * @return boolean value indicating user is manager or not.
     */
    boolean authManagerAccess(String userId);

    /**
     * Grant/revoke user's manager level.
     * @param userId User's id.
     * @param level Manager level.
     * @param enable true if grant, false if revoke.
     */
    void changeManagerLevel(String userId, ManagerLevel level, boolean enable);

    /**
     * Read accounts with paging.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime search constraints from.
     * @param to LocalDateTime search constraints to.
     * @return BoardAccountDTO.PagedAccounts object containing accounts.
     */
    BoardAccountDTO.PagedAccounts readAccountsPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Search accounts with paging.
     * @param mode Search mode.
     * @param keyword Search keyword.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime search constraints from.
     * @param to LocalDateTime search constraints to.
     * @return BoardAccountDTO.PagedAccounts object containing accounts.
     */
    BoardAccountDTO.PagedAccounts searchAccountsPage(AccountSearchMode mode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

    /**
     * Invalidate accounts.
     * @param accountIds List of account ids.
     */
    void invalidateAccounts(List<Long> accountIds);

    /**
     * Restore accounts.
     * @param accountIds List of account ids.
     */
    void restoreAccounts(List<Long> accountIds);

    /**
     * Change password of accounts.
     * @param accountIds List of account ids.
     * @param newPassword New password.
     */
    // TODO: generate unique password each and send mail or notify them to recover it.
    void changePassword(List<Long> accountIds, String newPassword);

    /**
     * Change username of accounts.
     * @param accountIds List of account ids.
     * @param newUsername New username.
     */
    void changeUsername(List<Long> accountIds, String newUsername);
}
