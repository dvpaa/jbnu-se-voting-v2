package jbnu.se.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidatePair {

    @NotNull(message = "선본 id를 입력해 주세요.")
    private Long headquarterId;

    @NotNull(message = "정후보 정보를 입력해 주세요.")
    @Valid
    private CandidateRequest president;

    @NotNull(message = "부후보 정보를 입력해 주세요.")
    @Valid
    private CandidateRequest vicePresident;

    @Builder
    public CandidatePair(Long headquarterId, CandidateRequest president, CandidateRequest vicePresident) {
        this.headquarterId = headquarterId;
        this.president = president;
        this.vicePresident = vicePresident;
    }
}
