package jbnu.se.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidElectionType;
import jbnu.se.api.validation.ValidationGroups.FirstGroup;
import jbnu.se.api.validation.ValidationGroups.SecondGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ElectionRequest {

    @NotBlank(message = "제목을 입력해 주세요.", groups = FirstGroup.class)
    private String title;

    @NotNull(message = "선거 기간을 입력해 주세요.", groups = FirstGroup.class)
    @Valid
    private PeriodRequest period;

    @NotNull(message = "선거 종류를 입력해 주세요.", groups = FirstGroup.class)
    @ValidElectionType(message = "유효하지 않은 선거 종류 입니다.", groups = SecondGroup.class)
    private String electionType;
}
