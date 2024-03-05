package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class Candidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "headquarter_id")
    private Headquarter headquarter;

    @Embedded
    private Member member;

    @Column(nullable = false)
    @Enumerated(STRING)
    private Grade grade;

    @Column(nullable = false)
    @Enumerated(STRING)
    private CandidateType type;

}
