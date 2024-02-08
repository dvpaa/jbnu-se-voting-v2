package jbnu.se.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Setter
public class Candidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "headquaters_id")
    private Headquaters headquaters;

    @Embedded
    private Member member;

    @Column(nullable = false)
    @Enumerated(STRING)
    private Grade grade;

    @Column(nullable = false)
    @Enumerated(STRING)
    private CandidateType type;

}
