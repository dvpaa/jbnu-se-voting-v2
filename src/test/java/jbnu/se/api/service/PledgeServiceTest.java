package jbnu.se.api.service;

import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Pledge;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.PledgeRepository;
import jbnu.se.api.request.PledgeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PledgeServiceTest {

    @Autowired
    private PledgeService pledgeService;

    @Autowired
    private PledgeRepository pledgeRepository;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @AfterEach
    void tearDown() {
        headquarterRepository.deleteAll();
        pledgeRepository.deleteAll();
    }

    @Test
    @DisplayName("공약 등록")
    void createPledgeTest() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        PledgeRequest pledgeRequest = new PledgeRequest();
        pledgeRequest.setHeadquarterId(savedHeadquarter.getId());
        pledgeRequest.setDescription("test-desc");

        // when
        pledgeService.createPledge(pledgeRequest);

        // then
        Headquarter byId = headquarterRepository.findById(savedHeadquarter.getId()).orElse(null);
        List<Pledge> all = pledgeRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getDescription()).isEqualTo("test-desc");
        assertThat(byId).isNotNull();
        assertThat(byId.getPledge().getId()).isEqualTo(all.get(0).getId());
    }
}