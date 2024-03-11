package jbnu.se.api.service;

import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.CandidateType;
import jbnu.se.api.domain.Grade;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.repository.CandidateRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.request.CandidatePairRequests;
import jbnu.se.api.request.CandidateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CandidateServiceTest {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @AfterEach
    void tearDown() {
        candidateRepository.deleteAll();
        headquarterRepository.deleteAll();
    }

    @Test
    @DisplayName("후보자를 선본에 등록한다.")
    void registerCandidateTest() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter saved = headquarterRepository.save(headquarter);

        CandidatePairRequests requests = new CandidatePairRequests();
        requests.setCandidates(
                Arrays.asList(
                        CandidatePair.builder()
                                .headquarterId(saved.getId())
                                .president(CandidateRequest.builder()
                                        .studentId("id1")
                                        .name("name1")
                                        .grade(Grade.FRESHMAN.name())
                                        .candidateType(CandidateType.PRESIDENT.name())
                                        .build())
                                .vicePresident(CandidateRequest.builder()
                                        .studentId("id2")
                                        .name("name2")
                                        .grade(Grade.SENIOR.name())
                                        .candidateType(CandidateType.VICE_PRESIDENT.name())
                                        .build())
                                .build()
                )
        );

        // when
        candidateService.registerCandidate(requests);

        // then
        List<Candidate> candidates = candidateRepository.findAll();
        assertThat(candidates).hasSize(2);

        candidates.forEach(candidate -> assertThat(candidate.getHeadquarter().getId()).isEqualTo(saved.getId()));
    }

    @Test
    @DisplayName("선본에 소속된 후보를 찾을 수 있다.")
    void getCandidatesByHeadquarterTest() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter saved = headquarterRepository.save(headquarter);

        CandidatePairRequests requests = new CandidatePairRequests();
        requests.setCandidates(
                Arrays.asList(
                        CandidatePair.builder()
                                .headquarterId(saved.getId())
                                .president(CandidateRequest.builder()
                                        .studentId("id1")
                                        .name("name1")
                                        .grade(Grade.FRESHMAN.name())
                                        .candidateType(CandidateType.PRESIDENT.name())
                                        .build())
                                .vicePresident(CandidateRequest.builder()
                                        .studentId("id2")
                                        .name("name2")
                                        .grade(Grade.SENIOR.name())
                                        .candidateType(CandidateType.VICE_PRESIDENT.name())
                                        .build())
                                .build()
                )
        );

        candidateService.registerCandidate(requests);

        // when
        List<Candidate> candidates = candidateService.getCandidatesByHeadquarter(saved.getId());

        // then
        assertThat(candidates).hasSize(2);
        candidates.forEach(candidate -> assertThat(candidate.getHeadquarter().getId()).isEqualTo(saved.getId()));
    }
}