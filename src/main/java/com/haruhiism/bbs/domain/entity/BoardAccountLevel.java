package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.AccountLevel;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "levelID")
@RequiredArgsConstructor
public class BoardAccountLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long levelID;
    @NonNull
    @Positive
    private Long accountID;
    @NonNull
    @Enumerated(EnumType.STRING) // to store enum types as string value.
    private AccountLevel accountLevel;

    public BoardAccountLevel() {}
}
