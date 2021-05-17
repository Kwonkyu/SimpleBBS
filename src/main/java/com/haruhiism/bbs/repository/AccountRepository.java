package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<BoardAccount, Long> {

    Page<BoardAccount> findAll(Pageable pageable);

    Optional<BoardAccount> findByUserId(String userId);
    Optional<BoardAccount> findByUserIdAndAvailableTrue(String userid);

    Page<BoardAccount> findAllByUserIdContainingAndCreatedDateTimeBetween(String userId, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardAccount> findAllByUsernameContainingAndCreatedDateTimeBetween(String username, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardAccount> findAllByEmailContainingAndCreatedDateTimeBetween(String email, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<BoardAccount> findAllByCreatedDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    boolean existsByUserId(String userid);

    void deleteByUserId(String userId);


}
