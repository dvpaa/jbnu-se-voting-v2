package jbnu.se.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbnu.se.api.annotation.ValidElectionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingRequest {

    @NotNull(message = "선거 id를 입력해 주세요.")
    private Long electionId;

    @ValidElectionType(message = "유효하지 않은 선거 종류 입니다.")
    private String electionType;

    /**
     * 단선: "agree", "disagree", "void"
     * 결선: 각 선본의 symbol
     */
    @NotBlank(message = "선거 결과를 입력해 주세요")
    private String result;
}
