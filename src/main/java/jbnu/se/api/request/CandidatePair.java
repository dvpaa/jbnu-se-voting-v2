package jbnu.se.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CandidatePair {

    private Long headquarterId;

    private CandidateRequest president;

    private CandidateRequest vicePresident;

    @Builder
    public CandidatePair(Long headquarterId, CandidateRequest president, CandidateRequest vicePresident) {
        this.headquarterId = headquarterId;
        this.president = president;
        this.vicePresident = vicePresident;
    }
}
