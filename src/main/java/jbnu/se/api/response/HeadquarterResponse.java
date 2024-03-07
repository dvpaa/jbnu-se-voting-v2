package jbnu.se.api.response;

import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.Headquarter;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
public class HeadquarterResponse {
    private final Long id;
    private final Long electionId;
    private final Long pledgeId;
    private final String name;

    private CandidatePairResponse candidatePair;

    public HeadquarterResponse(Headquarter headquarter) {
        this.id = headquarter.getId();
        this.electionId = headquarter.getElection().getId();
        if (headquarter.getPledge() == null) {
            this.pledgeId = null;
        } else {
            this.pledgeId = headquarter.getPledge().getId();
        }
        this.name = headquarter.getName();
    }

    @Builder
    public HeadquarterResponse(Long id, Long electionId, Long pledgeId, String name) {
        this.id = id;
        this.electionId = electionId;
        this.pledgeId = pledgeId;
        this.name = name;
    }

    public void setCandidatePair(List<Candidate> candidates) {
        candidates.sort(Comparator.comparing(Candidate::getType));

        Candidate president = candidates.get(0);
        Candidate vicePresident = candidates.get(1);

        this.candidatePair = CandidatePairResponse.builder()
                .president(new CandidateResponse(president))
                .vicePresident(new CandidateResponse(vicePresident))
                .build();
    }
}
