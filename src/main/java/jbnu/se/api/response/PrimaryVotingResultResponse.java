package jbnu.se.api.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PrimaryVotingResultResponse extends VotingResultResponse {

    private final List<HeadquarterResult> headquarterResults;

    @Builder
    public PrimaryVotingResultResponse(Long voterCount, Long voidCount, List<HeadquarterResult> headquarterResults) {
        super(voterCount, voidCount);
        this.headquarterResults = headquarterResults;
    }
}
