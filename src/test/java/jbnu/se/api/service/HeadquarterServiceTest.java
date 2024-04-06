package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.HeadquarterCreateRequest;
import jbnu.se.api.request.HeadquarterCreateRequests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HeadquarterServiceTest {

    @Autowired
    private HeadquarterService headquarterService;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        headquarterRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("선본 등록")
    void registerHeadquarterTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election saved = electionRepository.save(election);

        HeadquarterCreateRequest headquarterCreateRequest1 = HeadquarterCreateRequest.builder()
                .electionId(saved.getId())
                .name("test1")
                .symbol("1")
                .build();

        HeadquarterCreateRequest headquarterCreateRequest2 = HeadquarterCreateRequest.builder()
                .electionId(saved.getId())
                .name("test2")
                .symbol("2")
                .build();

        HeadquarterCreateRequests requests = new HeadquarterCreateRequests();
        requests.setHeadquarters(Arrays.asList(headquarterCreateRequest1, headquarterCreateRequest2));

        // when
        List<Headquarter> headquarters = headquarterService.registerHeadquarter(requests);

        // then
        assertThat(headquarterRepository.count()).isEqualTo(2);
        headquarters.forEach(headquarter -> assertThat(headquarter.getElection().getId()).isEqualTo(saved.getId()));
    }

    @Test
    @DisplayName("선거에 등록된 선본을 찾을 수 있다.")
    void getHeadquartersByElectionTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election saved = electionRepository.save(election);

        HeadquarterCreateRequest headquarterCreateRequest1 = HeadquarterCreateRequest.builder()
                .electionId(saved.getId())
                .name("test1")
                .symbol("1")
                .build();

        HeadquarterCreateRequest headquarterCreateRequest2 = HeadquarterCreateRequest.builder()
                .electionId(saved.getId())
                .name("test2")
                .symbol("2")
                .build();

        HeadquarterCreateRequests requests = new HeadquarterCreateRequests();
        requests.setHeadquarters(Arrays.asList(headquarterCreateRequest1, headquarterCreateRequest2));

        headquarterService.registerHeadquarter(requests);

        // when
        List<Headquarter> headquartersByElection = headquarterService.getHeadquartersByElection(saved.getId());

        // then
        assertThat(headquartersByElection).hasSize(2);
        headquartersByElection.forEach(headquarter -> assertThat(headquarter.getElection().getId()).isEqualTo(saved.getId()));
    }
}