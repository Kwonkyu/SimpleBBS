package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.ManagerLevel;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BoardAccountLevel implements GrantedAuthority {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ACCOUNT_LEVEL_ID")
    private long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "BOARD_ACCOUNT_ID")
    private BoardAccount boardAccount;

    @NonNull
    @Enumerated(EnumType.STRING) // to store enum types as string value.
    @Column(name = "ACCOUNT_LEVEL")
    private ManagerLevel accountLevel;

    @Override
    public String getAuthority() {
        return accountLevel.name();
    }
}
