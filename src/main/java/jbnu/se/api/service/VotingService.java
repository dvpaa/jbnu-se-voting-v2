package jbnu.se.api.service;

import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Member;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.VotingResultResponse;

public interface VotingService {
    void vote(Member member, VotingRequest votingRequest);

    boolean supports(ElectionType type);

    boolean validResult(VotingRequest votingRequest);

    VotingResultResponse getVotingResult(VotingResultRequest votingResultRequest);
}
