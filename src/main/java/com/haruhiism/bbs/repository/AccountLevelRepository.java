package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.ManagerLevel;
import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountLevelRepository extends CrudRepository<BoardAccountLevel, Long> {

    List<BoardAccountLevel> findAllByBoardAccount(BoardAccount account);

    void deleteByBoardAccountAndAccountLevel(BoardAccount boardAccount, ManagerLevel level);
    void deleteAllByBoardAccount(BoardAccount boardAccount);

}
