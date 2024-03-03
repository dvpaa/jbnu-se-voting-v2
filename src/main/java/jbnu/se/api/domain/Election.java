package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Election extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Embedded
    private Period period;

    @Column(nullable = false)
    @Enumerated(STRING)
    private ElectionType electionType;

    @Builder
    public Election(String title, Period period, ElectionType electionType, String createdBy, LocalDateTime createdDate) {
        this.title = title;
        this.period = period;
        this.electionType = electionType;
        super.setCreatedBy(createdBy);
        super.setCreatedDate(createdDate);
    }
}
