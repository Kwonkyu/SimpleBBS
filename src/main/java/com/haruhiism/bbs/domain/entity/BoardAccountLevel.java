package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.AccountLevel;
import lombok.*;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardAccountLevel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ACCOUNT_LEVEL_ID")
    private Long id;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "BOARD_ACCOUNT_ID")
    private BoardAccount boardAccount;
    @NonNull
    @Enumerated(EnumType.STRING) // to store enum types as string value.
    @Column(name = "ACCOUNT_LEVEL")
    private AccountLevel accountLevel;

}
