package jbnu.se.api.util;

import jbnu.se.api.exception.InvalidElectionTypeException;
import jbnu.se.api.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VotingServiceFinder {

    private final List<VotingService> votingServices;

    public VotingService find(String type) {
        return votingServices.stream()
                .filter(votingService -> votingService.supports(type))
                .findAny()
                .orElseThrow(InvalidElectionTypeException::new);
    }
}
