package jbnu.se.api.service;

import jbnu.se.api.domain.Member;
import jbnu.se.api.request.VotingRequest;

public interface VotingService {
    void vote(Member member, VotingRequest votingRequest);

    boolean supports(String type);

    boolean validResult(VotingRequest votingRequest);
}
