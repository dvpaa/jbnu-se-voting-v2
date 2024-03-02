package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;

    public void registerElection(String userId, ElectionRequest request) {
        Election election = Election.builder()
                .title(request.getTitle())
                .period(new Period(request.getStartDate(), request.getEndDate()))
                .electionType(request.getElectionType())
                .createdBy(userId)
                .createdDate(now())
                .build();

        electionRepository.save(election);
    }
}