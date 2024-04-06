package jbnu.se.api.domain;

import jakarta.persistence.*;
import jbnu.se.api.request.CandidateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    public Candidate(CandidateRequest request, Headquarter headquarter) {
        this.headquarter = headquarter;
        this.member = new Member(request.getStudentId(), request.getName());
        this.grade = Grade.valueOf(request.getGrade());
        this.type = CandidateType.valueOf(request.getCandidateType());
    }

    @Builder
    public Candidate(Headquarter headquarter, Member member, Grade grade, CandidateType type) {
        this.headquarter = headquarter;
        this.member = member;
        this.grade = grade;
        this.type = type;
    }
}
