package com.haruhiism.bbs.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardAccountChallenge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ACCOUNT_CHALLENGE_ID")
    private Long id;

    @Column(name = "RECOVERY_THRESHOLD")
    private int count = 0;

    @NonNull
    @Column(name = "CHALLENGE_TIME")
    private LocalDateTime lastChallengeTime;


    public boolean challenge(){
        count = lastChallengeTime.isBefore(LocalDateTime.now().minusHours(1)) ? 0 : count+1;
        lastChallengeTime = LocalDateTime.now();
        return count <= 5;
    }


}