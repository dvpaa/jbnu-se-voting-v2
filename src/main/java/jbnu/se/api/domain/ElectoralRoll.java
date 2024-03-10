package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class ElectoralRoll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    @Embedded
    private Member member;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean voted;

    @PrePersist
    public void prePersist() {
        if (voted == null) {
            voted = false;
        }
    }
}
