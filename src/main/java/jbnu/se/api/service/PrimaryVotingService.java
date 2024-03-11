package jbnu.se.api.service;

import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrimaryVotingService extends AbstractVotingService {

    public PrimaryVotingService(VotingRepository votingRepository, ElectionRepository electionRepository, ElectoralRollRepository electoralRollRepository, HeadquarterRepository headquarterRepository) {
        super(votingRepository, electionRepository, electoralRollRepository, headquarterRepository);
    }

    @Override
    public boolean supports(String type) {
        return ElectionType.PRIMARY.name().equals(type);
    }

    @Override
    public boolean validResult(VotingRequest votingRequest) {
        List<Headquarter> headquarters = headquarterRepository.findAllByElectionId(votingRequest.getElectionId());

        List<String> symbols = headquarters.stream()
                .map(Headquarter::getSymbol)
                .toList();

        String result = votingRequest.getResult();

        return result.equals("void") || symbols.contains(result);
    }
}
