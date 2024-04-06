package jbnu.se.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CandidatePairResponse {
    private final CandidateResponse president;
    private final CandidateResponse vicePresident;

    @Builder
    public CandidatePairResponse(CandidateResponse president, CandidateResponse vicePresident) {
        this.president = president;
        this.vicePresident = vicePresident;
    }
}
