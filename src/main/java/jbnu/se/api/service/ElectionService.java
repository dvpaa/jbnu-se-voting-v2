package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Period;
import jbnu.se.api.exception.ElectionNotFound;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.response.ElectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;

    public void registerElection(String userId, ElectionRequest request) {
        Election election = Election.builder()
                .title(request.getTitle())
                .period(new Period(request.getPeriod().getStartDate(), request.getPeriod().getEndDate()))
                .electionType(ElectionType.valueOf(request.getElectionType()))
                .createdBy(userId)
                .createdDate(now())
                .build();

        electionRepository.save(election);
    }

    public List<ElectionResponse> findAllElections() {
        return electionRepository.findAll()
                .stream()
                .map(ElectionResponse::new)
                .toList();
    }

    public ElectionResponse findElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(ElectionNotFound::new);

        return new ElectionResponse(election);
    }
}
