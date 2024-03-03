package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.Period;
import jbnu.se.api.exception.ElectionNotFound;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.response.ElectionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.of;
import static jbnu.se.api.domain.ElectionType.SINGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        request.setElectionType(SINGLE.name());
        LocalDateTime startDate = of(2024, 2, 29, 19, 0);
        LocalDateTime endDate = startDate.plusDays(1);
        request.getPeriod().setStartDate(startDate);
        request.getPeriod().setEndDate(endDate);

        // when
        electionService.registerElection("1", request);

        // then
        assertThat(electionRepository.count()).isEqualTo(1L);
        Election election = electionRepository.findAll().get(0);
        assertThat(election.getCreatedBy()).isEqualTo("1");
        assertThat(election.getPeriod()).isEqualTo(new Period(request.getPeriod().getStartDate(), request.getPeriod().getEndDate()));
        assertThat(election.getElectionType().name()).isEqualTo(request.getElectionType());
    }

    @Test
    @DisplayName("모든 선거 조회")
    void findAllElectionsTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2024, 3, 2, 0, 0), of(2024, 3, 3, 0, 0)))
                .electionType(SINGLE)
                .build();

        // when
        electionRepository.save(election);
        List<ElectionResponse> elections = electionService.findAllElections();

        assertThat(elections).hasSize(1);
    }

    @Test
    @DisplayName("단일 선거 조회")
    void findElectionTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2024, 3, 2, 0, 0), of(2024, 3, 3, 0, 0)))
                .electionType(SINGLE)
                .build();

        // when
        Election saved = electionRepository.save(election);
        Long id = saved.getId();
        ElectionResponse electionById = electionService.findElectionById(id);

        // then
        assertThat(id).isEqualTo(electionById.getId());
    }

    @Test
    @DisplayName("존재하지 않는 선거를 조회시 에러 반환")
    void electionNotFoundTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2024, 3, 2, 0, 0), of(2024, 3, 3, 0, 0)))
                .electionType(SINGLE)
                .build();

        // when
        Election saved = electionRepository.save(election);
        Long id = saved.getId();

        // then
        assertThatThrownBy(() -> electionService.findElectionById(id + 1))
                .isInstanceOf(ElectionNotFound.class);
    }
}