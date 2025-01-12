package jbnu.se.api.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.exception.UnmatchedElectionTypeException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingResultRequest;
import jbnu.se.api.response.HeadquarterResult;
import jbnu.se.api.response.PrimaryVotingResultResponse;
import jbnu.se.api.response.VotingResultResponse;
import org.springframework.stereotype.Service;

@Service
public class PrimaryVotingService extends AbstractVotingService {

    public PrimaryVotingService(VotingRepository votingRepository, ElectionRepository electionRepository,
                                ElectoralRollRepository electoralRollRepository,
                                HeadquarterRepository headquarterRepository) {
        super(votingRepository, electionRepository, electoralRollRepository, headquarterRepository);
    }

    @Override
    public boolean supports(ElectionType type) {
        return ElectionType.PRIMARY.equals(type);
    }

    @Override
    public boolean validResult(Long electionId, String result) {
        List<Headquarter> headquarters = headquarterRepository.findAllByElectionId(electionId);

        Set<String> symbols = headquarters.stream()
                .map(Headquarter::getSymbol)
                .collect(Collectors.toSet());

        return result.equals("void") || symbols.contains(result);
    }

    @Override
    public VotingResultResponse getVotingResult(VotingResultRequest votingResultRequest) {
        Election election = electionRepository.findById(votingResultRequest.getElectionId())
                .orElseThrow(ElectionNotFoundException::new);

        if (!election.getElectionType().name().equals(votingResultRequest.getElectionType())) {
            throw new UnmatchedElectionTypeException();
        }

        Long voterCount = electoralRollRepository.countByElectionId(votingResultRequest.getElectionId());
        Long voidCount = votingRepository.countByElectionIdAndResult(votingResultRequest.getElectionId(), "void");

        List<Headquarter> headquarters = headquarterRepository.findAllByElectionIdOrderBySymbol(
                votingResultRequest.getElectionId());

        List<HeadquarterResult> headquarterResults = headquarters.stream()
                .map(headquarter -> {
                    Long count = votingRepository.countByElectionIdAndResult(votingResultRequest.getElectionId(),
                            headquarter.getSymbol());
                    return HeadquarterResult.builder()
                            .symbol(headquarter.getSymbol())
                            .name(headquarter.getName())
                            .count(count)
                            .build();
                })
                .toList();

        return PrimaryVotingResultResponse.builder()
                .voterCount(voterCount)
                .voidCount(voidCount)
                .headquarterResults(headquarterResults)
                .build();
    }
}
