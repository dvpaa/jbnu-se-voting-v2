package jbnu.se.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidateRequest {

    private String studentId;

    private String name;

    private String grade;

    private String candidateType;

    @Builder
    public CandidateRequest(String studentId, String name, String grade, String candidateType) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.candidateType = candidateType;
    }
}
