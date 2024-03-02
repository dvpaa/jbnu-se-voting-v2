package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElectionServiceTest {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("선거 등록")
    void registerElectionTest() {
        // given
        ElectionRequest request = new ElectionRequest();
        request.setTitle("test");
        request.setElectionType(ElectionType.SINGLE);
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 29, 19, 0);
        LocalDateTime endDate = startDate.plusDays(1);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        // when
        electionService.registerElection("1", request);

        // then
        assertThat(electionRepository.count()).isEqualTo(1L);
        Election election = electionRepository.findAll().get(0);
        assertThat(election.getCreatedBy()).isEqualTo("1");
        assertThat(election.getPeriod()).isEqualTo(new Period(request.getStartDate(), request.getEndDate()));
        assertThat(election.getElectionType()).isEqualTo(request.getElectionType());
    }
}