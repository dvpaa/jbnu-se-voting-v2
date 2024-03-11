package jbnu.se.api.service;

import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
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
}
