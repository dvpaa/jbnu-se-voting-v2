package jbnu.se.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SingleVotingResultResponse extends VotingResultResponse {

    private final Long agreeCount;

    private final Long disagreeCount;

    @Builder
    public SingleVotingResultResponse(Long voterCount, Long voidCount, Long agreeCount, Long disagreeCount) {
        super(voterCount, voidCount);
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
    }
}
