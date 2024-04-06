package jbnu.se.api.response;

import jbnu.se.api.domain.Candidate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CandidateResponse {
    private final Long id;
    private final String studentId;
    private final String name;
    private final String grade;

    public CandidateResponse(Candidate candidate) {
        this.id = candidate.getId();
        this.studentId = candidate.getMember().getStudentId();
        this.name = candidate.getMember().getName();
        this.grade = candidate.getGrade().name();
    }

    @Builder
    public CandidateResponse(Long id, String studentId, String name, String grade) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
    }
}
