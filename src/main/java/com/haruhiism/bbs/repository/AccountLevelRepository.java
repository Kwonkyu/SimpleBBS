package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import com.haruhiism.bbs.domain.entity.BoardAccountLevel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLevelRepository extends CrudRepository<BoardAccountLevel, Long> {

    public void deleteAllByBoardAccount(BoardAccount boardAccount);
}
