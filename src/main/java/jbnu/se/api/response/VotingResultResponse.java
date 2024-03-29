package jbnu.se.api.response;

import lombok.Getter;

@Getter
public abstract class VotingResultResponse {
    private final Long voterCount;

    private final Long voidCount;

    protected VotingResultResponse(Long voterCount, Long voidCount) {
        this.voterCount = voterCount;
        this.voidCount = voidCount;
    }
}