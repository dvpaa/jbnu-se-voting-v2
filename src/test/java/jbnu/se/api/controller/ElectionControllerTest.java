package jbnu.se.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.service.ElectionService;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ElectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("관리자 권한이 없으면 선거 등록 불가")
    void roleTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 29, 16, 0);
        LocalDateTime endDate = startDate.plusDays(1);
        electionRequest.setStartDate(startDate);
        electionRequest.setEndDate(endDate);
        electionRequest.setElectionType(ElectionType.SINGLE);

        mockMvc.perform(post("/api/elections")
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자가 선거를 정상적으로 등록한다.")
    void test() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        LocalDateTime startDate = LocalDateTime.of(2024, 2, 29, 16, 0);
        LocalDateTime endDate = startDate.plusDays(1);
        electionRequest.setStartDate(startDate);
        electionRequest.setEndDate(endDate);
        electionRequest.setElectionType(ElectionType.SINGLE);

        // when
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        // then
        assertThat(electionRepository.count()).isEqualTo(1L);

        Election election = electionRepository.findAll().get(0);
        assertThat(election.getTitle()).isEqualTo(electionRequest.getTitle());
        assertThat(election.getPeriod().getStartDate()).isEqualTo(electionRequest.getStartDate());
        assertThat(election.getPeriod().getEndDate()).isEqualTo(electionRequest.getEndDate());
    }
}