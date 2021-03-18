package com.haruhiism.bbs.domain.entity;

import com.haruhiism.bbs.domain.AccountLevel;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "accountID")
@Entity
@RequiredArgsConstructor
public class BoardAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountID;
    @NonNull
    @NotBlank
    private String userid;
    @NonNull
    @NotBlank
    @Length(min = 1)
    private String username;
    @NonNull
    @NotBlank
    @Length(min = 4)
    private String password;
    @NonNull
    @NotBlank
    @Email
    private String email;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // load account info and account level info eagerly.
    @JoinColumn(name = "accountID")
    private List<BoardAccountLevel> levels;

    public BoardAccount() {}
}
