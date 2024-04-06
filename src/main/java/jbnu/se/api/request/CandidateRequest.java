package jbnu.se.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidCandidateType;
import jbnu.se.api.annotation.ValidGrade;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jbnu.se.api.validation.ValidationGroups.FirstGroup;
import static jbnu.se.api.validation.ValidationGroups.SecondGroup;

@Getter
@Setter
@NoArgsConstructor
public class CandidateRequest {

    @NotBlank(message = "학번을 입력해 주세요.", groups = FirstGroup.class)
    private String studentId;

    @NotBlank(message = "이름을 입력해 주세요.", groups = FirstGroup.class)
    private String name;

    @NotNull(message = "학년을 입력해 주세요.", groups = FirstGroup.class)
    @ValidGrade(message = "유효하지 않은 학년입니다.", groups = SecondGroup.class)
    private String grade;

    @NotNull(message = "선거 종류를 입력해 주세요.", groups = FirstGroup.class)
    @ValidCandidateType(message = "유효하지 않은 후보 종류입니다.", groups = SecondGroup.class)
    private String candidateType;

    @Builder
    public CandidateRequest(String studentId, String name, String grade, String candidateType) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.candidateType = candidateType;
    }
}
