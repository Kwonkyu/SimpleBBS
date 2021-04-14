package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<BoardAccount, Long> {

    public Optional<BoardAccount> findByUserId(String userid);

    public boolean existsByUserId(String userid);
}
