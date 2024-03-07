package jbnu.se.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.CandidateType;
import jbnu.se.api.domain.Grade;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.repository.CandidateRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.request.CandidateRequest;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("후보 등록에는 관리자 권한이 있어야 한다.")
    void candidateRegisterRoleTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        Headquarter saved = headquarterRepository.save(headquarter);

        List<CandidatePair> candidatePairs = Arrays.asList(
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
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidatePairs))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자는 후보를 정상적으로 등록한다.")
    void registerCandidateTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        Headquarter saved = headquarterRepository.save(headquarter);

        List<CandidatePair> candidatePairs = Arrays.asList(
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
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidatePairs))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Candidate> candidates = candidateRepository.findAll();

        assertThat(candidates).hasSize(2);
        candidates.forEach(candidate -> assertThat(candidate.getHeadquarter().getId()).isEqualTo(saved.getId()));
    }


}