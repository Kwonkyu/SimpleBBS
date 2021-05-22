package com.haruhiism.bbs.repository;

import com.haruhiism.bbs.domain.entity.BoardAccountChallenge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountChallengeRepository extends CrudRepository<BoardAccountChallenge, Long> {

}
