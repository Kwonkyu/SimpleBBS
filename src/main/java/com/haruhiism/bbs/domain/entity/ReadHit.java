package com.haruhiism.bbs.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ReadHit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HIT_ID")
    private Long id;
    @Column(name = "HIT_VALUE")
    private int hit = 0;

    public void increaseHit(){
        hit++;
    }

    public void clearHit(){
        hit = 0;
    }
}
