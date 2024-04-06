package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.exception.UnmatchedElectionTypeException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.SingleVotingResultResponse;
import jbnu.se.api.response.VotingResultResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class SingleVotingService extends AbstractVotingService {

    private static final Set<String> RESULT_SET = new HashSet<>(Arrays.asList("agree", "disagree", "void"));

    public SingleVotingService(VotingRepository votingRepository, ElectionRepository electionRepository, ElectoralRollRepository electoralRollRepository, HeadquarterRepository headquarterRepository) {
        super(votingRepository, electionRepository, electoralRollRepository, headquarterRepository);
    }

    @Override
    public boolean supports(String type) {
        return ElectionType.SINGLE.name().equals(type);
    }

    @Override
    public boolean validResult(VotingRequest votingRequest) {
        return RESULT_SET.contains(votingRequest.getResult());
    }

    @Override
    public VotingResultResponse getVotingResult(VotingResultRequest votingResultRequest) {
        Election election = electionRepository.findById(votingResultRequest.getElectionId())
                .orElseThrow(ElectionNotFoundException::new);

        if (!election.getElectionType().name().equals(votingResultRequest.getElectionType())) {
            throw new UnmatchedElectionTypeException();
        }

        Long voterCount = electoralRollRepository.countByElectionId(votingResultRequest.getElectionId());
        Long agreeCount = votingRepository.countByElectionIdAndResult(votingResultRequest.getElectionId(), "agree");
        Long disagreeCount = votingRepository.countByElectionIdAndResult(votingResultRequest.getElectionId(), "disagree");
        Long voidCount = votingRepository.countByElectionIdAndResult(votingResultRequest.getElectionId(), "void");

        return SingleVotingResultResponse.builder()
                .voterCount(voterCount)
                .agreeCount(agreeCount)
                .disagreeCount(disagreeCount)
                .voidCount(voidCount)
                .build();
    }
}
