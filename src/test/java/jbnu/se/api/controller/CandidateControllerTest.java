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
import jbnu.se.api.request.CandidatePairRequests;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Candidate> candidates = candidateRepository.findAll();

        assertThat(candidates).hasSize(2);
        candidates.forEach(candidate -> assertThat(candidate.getHeadquarter().getId()).isEqualTo(saved.getId()));
    }

    @Test
    @DisplayName("후보 등록 요청시 후보 정보가 존재해야 한다.")
    void candidateRequestsValidationTest() throws Exception {
        // given
        CandidatePairRequests requests = new CandidatePairRequests();
        requests.setCandidates(
                Arrays.asList(
                        CandidatePair.builder()
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation['candidates[0].headquarterId']").value("선본 id를 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].president']").value("정후보 정보를 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident']").value("부후보 정보를 입력해 주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("후보 정보에 값들이 존재해야 한다.")
    void candidateRequestValidationTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        Headquarter saved = headquarterRepository.save(headquarter);

        CandidatePairRequests requests = new CandidatePairRequests();
        requests.setCandidates(
                Arrays.asList(
                        CandidatePair.builder()
                                .headquarterId(saved.getId())
                                .president(CandidateRequest.builder()
                                        .build())
                                .vicePresident(CandidateRequest.builder()
                                        .build())
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation['candidates[0].president.name']").value("이름을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].president.studentId']").value("학번을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].president.grade']").value("학년을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].president.candidateType']").value("선거 종류를 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.name']").value("이름을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.studentId']").value("학번을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.grade']").value("학년을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.candidateType']").value("선거 종류를 입력해 주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("유효하지 않은 학년은 입력될 수 없다.")
    void gradeValidationTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
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
                                        .grade("test1")
                                        .candidateType(CandidateType.PRESIDENT.name())
                                        .build())
                                .vicePresident(CandidateRequest.builder()
                                        .studentId("id2")
                                        .name("name2")
                                        .grade("test2")
                                        .candidateType(CandidateType.VICE_PRESIDENT.name())
                                        .build())
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation['candidates[0].president.grade']").value("유효하지 않은 학년입니다."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.grade']").value("유효하지 않은 학년입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("유효하지 않은 후보 종류는 입력될 수 없다.")
    void candidateTypeValidationTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
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
                                        .grade(Grade.SOPHOMORE.name())
                                        .candidateType("test1")
                                        .build())
                                .vicePresident(CandidateRequest.builder()
                                        .studentId("id2")
                                        .name("name2")
                                        .grade(Grade.SENIOR.name())
                                        .candidateType("test2")
                                        .build())
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/candidates")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation['candidates[0].president.candidateType']").value("유효하지 않은 후보 종류입니다."))
                .andExpect(jsonPath("$.validation['candidates[0].vicePresident.candidateType']").value("유효하지 않은 후보 종류입니다."))
                .andDo(print());
    }

}