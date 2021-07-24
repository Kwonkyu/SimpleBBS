package com.haruhiism.bbs.service.manage;

import com.haruhiism.bbs.domain.ManagerLevel;

import java.time.LocalDateTime;
import java.util.List;

import static com.haruhiism.bbs.domain.dto.BoardAccountDTO.*;
import static com.haruhiism.bbs.command.manage.AccountLevelManagementCommand.*;

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
     * @param operation grant or revoke.
     */
    void changeManagerLevel(String userId, ManagerLevel level, LevelOperation operation);

    /**
     * Read accounts with paging.
     * @param pageNum Page's number.
     * @param pageSize Page's size.
     * @param from LocalDateTime search constraints from.
     * @param to LocalDateTime search constraints to.
     * @return BoardAccountDTO.PagedAccounts object containing accounts.
     */
    PagedAccounts readAccountsPage(int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

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
    PagedAccounts searchAccountsPage(AccountSearchMode mode, String keyword, int pageNum, int pageSize, LocalDateTime from, LocalDateTime to);

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
