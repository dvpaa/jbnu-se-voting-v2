package jbnu.se.api.service;

import jakarta.transaction.Transactional;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.HeadquarterCreateRequest;
import jbnu.se.api.request.HeadquarterCreateRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeadquarterService {

    private final HeadquarterRepository headquarterRepository;

    private final ElectionRepository electionRepository;

    @Transactional
    public List<Headquarter> registerHeadquarter(HeadquarterCreateRequests headquarterCreateRequests) {

        return headquarterCreateRequests.getHeadquarters()
                .stream()
                .map(request -> {
                    Headquarter headquarterFromRequest = makeHeadquarterFromRequest(request);
                    return headquarterRepository.save(headquarterFromRequest);
                })
                .toList();
    }

    private Headquarter makeHeadquarterFromRequest(HeadquarterCreateRequest request) {
        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(ElectionNotFoundException::new);

        return Headquarter.builder()
                .election(election)
                .name(request.getName())
                .symbol(request.getSymbol())
                .build();
    }

    public List<Headquarter> getHeadquartersByElection(Long electionId) {
        return headquarterRepository.findAllByElectionId(electionId);
    }
}
