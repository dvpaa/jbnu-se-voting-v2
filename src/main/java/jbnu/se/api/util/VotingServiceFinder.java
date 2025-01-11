package jbnu.se.api.util;

import java.util.List;
import jbnu.se.api.domain.Election;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.exception.InvalidElectionTypeException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotingServiceFinder {

    private final List<VotingService> votingServices;

    private final ElectionRepository electionRepository;

    public VotingService find(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(ElectionNotFoundException::new);

        return votingServices.stream()
                .filter(votingService -> votingService.supports(election.getElectionType()))
                .findAny()
                .orElseThrow(InvalidElectionTypeException::new);
    }
}
