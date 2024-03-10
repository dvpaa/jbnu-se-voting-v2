package jbnu.se.api.service;

import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.request.ElectoralRollRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElectoralRollServiceTest {

    @Autowired
    private ElectoralRollService electoralRollService;

    @Autowired
    private ElectoralRollRepository electoralRollRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        electoralRollRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("선거인 명부 등록")
    void registerElectoralRollTest() {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "학번,이름\n201911111,name1".getBytes()
        );

        ElectoralRollRequest electoralRollRequest = new ElectoralRollRequest();
        electoralRollRequest.setElectionId(savedElection.getId());
        electoralRollRequest.setFile(file);

        // when
        electoralRollService.registerElectoralRoll(electoralRollRequest);

        // then
        List<ElectoralRoll> all = electoralRollRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
        assertThat(all.get(0).getMember().getStudentId()).isEqualTo("201911111");
        assertThat(all.get(0).getMember().getName()).isEqualTo("name1");
    }
}