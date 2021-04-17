package com.haruhiism.bbs.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class MACDate {
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDateTime;
    @Column(name = "MODIFIED_DATE")
    @LastModifiedDate
    private LocalDateTime modifiedDateTime;
}
