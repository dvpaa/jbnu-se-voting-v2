package jbnu.se.api.service;

import jakarta.transaction.Transactional;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Member;
import jbnu.se.api.domain.Voting;
import jbnu.se.api.exception.*;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractVotingService implements VotingService {

    protected final VotingRepository votingRepository;

    protected final ElectionRepository electionRepository;

    protected final ElectoralRollRepository electoralRollRepository;

    protected final HeadquarterRepository headquarterRepository;

    @Override
    @Transactional
    public void vote(Member member, VotingRequest votingRequest) {
        Election election = electionRepository.findById(votingRequest.getElectionId())
                .orElseThrow(ElectionNotFoundException::new);

        if (isElectionTypeMatched(votingRequest, election)) {
            throw new UnmatchedElectionTypeException();
        }

        if (!validResult(votingRequest)) {
            throw new InvalidVotingResultException();
        }

        ElectoralRoll electoralRoll = electoralRollRepository.findByElectionIdAndMember(election.getId(), member)
                .orElseThrow(ElectoralRollNotFoundException::new);

        if (isAlreadyVoted(electoralRoll)) {
            throw new AlreadyVotedException();
        }

        electoralRoll.setVoted(true);

        Voting voting = Voting.builder()
                .election(election)
                .result(votingRequest.getResult())
                .build();

        votingRepository.save(voting);
    }

    private boolean isAlreadyVoted(ElectoralRoll electoralRoll) {
        return Boolean.TRUE.equals(electoralRoll.getVoted());
    }

    private boolean isElectionTypeMatched(VotingRequest votingRequest, Election election) {
        return !election.getElectionType().name().equals(votingRequest.getElectionType());
    }
}
