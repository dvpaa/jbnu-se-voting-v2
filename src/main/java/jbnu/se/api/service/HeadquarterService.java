package jbnu.se.api.service;

import jakarta.transaction.Transactional;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.HeadquarterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadquarterService {

    private final HeadquarterRepository headquarterRepository;

    private final ElectionRepository electionRepository;

    @Transactional
    public List<Headquarter> registerHeadquarter(List<HeadquarterRequest> headquarterRequest) {

        return headquarterRequest.stream()
                .map(request -> {
                    Headquarter headquarterFromRequest = makeHeadquarterFromRequest(request);
                    return headquarterRepository.save(headquarterFromRequest);
                })
                .toList();
    }

    private Headquarter makeHeadquarterFromRequest(HeadquarterRequest request) {
        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(ElectionNotFoundException::new);

        return Headquarter.builder()
                .election(election)
                .name(request.getName())
                .build();
    }
}
