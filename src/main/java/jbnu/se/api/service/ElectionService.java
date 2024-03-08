package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Period;
import jbnu.se.api.exception.ElectionNotFoundException;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.response.ElectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;

    public Election registerElection(String userId, ElectionRequest electionRequest) {
        Election electionFromRequest = makeElectionFromRequest(userId, electionRequest);
        return electionRepository.save(electionFromRequest);
    }

    public List<ElectionResponse> findAllElections() {
        return electionRepository.findAll()
                .stream()
                .map(ElectionResponse::new)
                .toList();
    }

    public ElectionResponse findElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(ElectionNotFoundException::new);

        return new ElectionResponse(election);
    }

    private Election makeElectionFromRequest(String userId, ElectionRequest request) {
        return Election.builder()
                .title(request.getTitle())
                .period(new Period(request.getPeriod().getStartDate(), request.getPeriod().getEndDate()))
                .electionType(ElectionType.valueOf(request.getElectionType()))
                .createdBy(userId)
                .build();
    }
}
