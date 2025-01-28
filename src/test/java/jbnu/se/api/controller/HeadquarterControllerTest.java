package jbnu.se.api.controller;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jbnu.se.api.domain.Candidate;
import jbnu.se.api.domain.CandidateType;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Grade;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.CandidateRepository;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.CandidatePair;
import jbnu.se.api.request.CandidateRequest;
import jbnu.se.api.request.ElectionIdRequest;
import jbnu.se.api.request.HeadquarterCreateRequest;
import jbnu.se.api.request.HeadquarterCreateRequests;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HeadquarterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @AfterEach
    void tearDown() {
        candidateRepository.deleteAll();
        headquarterRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "name")
    @DisplayName("선본 등록에는 관리자 권한이 있어야 한다.")
    void headquarterRegisterRoleTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election saved = electionRepository.save(election);

        List<HeadquarterCreateRequest> request = Arrays.asList(
                HeadquarterCreateRequest.builder()
                        .electionId(saved.getId())
                        .name("test1")
                        .build(),
                HeadquarterCreateRequest.builder()
                        .electionId(saved.getId())
                        .name("test2")
                        .build()
        );

        // expected
        mockMvc.perform(post("/api/admin/headquarters")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("관리자는 선본을 정상적으로 등록한다.")
    void registerHeadquarterTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election saved = electionRepository.save(election);

        HeadquarterCreateRequests request = new HeadquarterCreateRequests();
        request.setHeadquarters(
                Arrays.asList(
                        HeadquarterCreateRequest.builder()
                                .electionId(saved.getId())
                                .name("test1")
                                .symbol("1")
                                .build(),
                        HeadquarterCreateRequest.builder()
                                .electionId(saved.getId())
                                .name("test2")
                                .symbol("2")
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/admin/headquarters")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Headquarter> headquarters = headquarterRepository.findAll();

        assertThat(headquarters).hasSize(2);
        headquarters.forEach(headquarter -> assertThat(headquarter.getElection().getId()).isEqualTo(saved.getId()));
    }


    @Test
    @WithMockUser(username = "name")
    @DisplayName("선본에 등록된 후보를 가져온다.")
    void getHeadquartersByElectionTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .election(savedElection)
                .name("test")
                .symbol("1")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        CandidatePair candidatePairs = CandidatePair.builder()
                .headquarterId(savedHeadquarter.getId())
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
                .build();

        CandidateRequest presidentInfo = candidatePairs.getPresident();
        CandidateRequest vicePresidentInfo = candidatePairs.getVicePresident();
        Candidate president = new Candidate(presidentInfo, savedHeadquarter);
        Candidate vicePresident = new Candidate(vicePresidentInfo, savedHeadquarter);

        Candidate savedPresident = candidateRepository.save(president);
        Candidate savedVicePresident = candidateRepository.save(vicePresident);

        ElectionIdRequest electionIdRequest = new ElectionIdRequest();
        electionIdRequest.setElectionId(savedElection.getId());

        // expected
        mockMvc.perform(get("/api/headquarters", savedElection.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionIdRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].electionId").value(savedElection.getId()))
                .andExpect(jsonPath("$[0].candidatePair.president.id").value(savedPresident.getId()))
                .andExpect(jsonPath("$[0].candidatePair.vicePresident.id").value(savedVicePresident.getId()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("선본 등록 요청시 값이 존재해야 한다.")
    void headquarterRequestValidationTest() throws Exception {
        // given
        HeadquarterCreateRequests request = new HeadquarterCreateRequests();
        request.setHeadquarters(
                Collections.singletonList(
                        HeadquarterCreateRequest.builder()
                                .build()
                )
        );

        // expected
        mockMvc.perform(post("/api/admin/headquarters")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation['headquarters[0].electionId']").value("선거 id를 입력해 주세요."))
                .andExpect(jsonPath("$.validation['headquarters[0].name']").value("선본 이름을 입력해 주세요."))
                .andExpect(jsonPath("$.validation['headquarters[0].symbol']").value("기호를 입력해 주세요."))
                .andDo(print());
    }
}