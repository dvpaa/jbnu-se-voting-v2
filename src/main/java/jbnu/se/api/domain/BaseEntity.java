package jbnu.se.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    private String createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    private String lastModifiedBy;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
