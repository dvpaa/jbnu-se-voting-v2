package jbnu.se.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.*;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.VotingRepository;
import jbnu.se.api.request.VotingRequest;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.time.LocalDateTime.of;
import static jbnu.se.api.config.SampleAuthInfo.USER_ID;
import static jbnu.se.api.config.SampleAuthInfo.USER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VotingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VotingRepository votingRepository;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private ElectoralRollRepository electoralRollRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        votingRepository.deleteAll();
        headquarterRepository.deleteAll();
        electoralRollRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("단선 투표")
    void singleVoteTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("SINGLE");
        votingRequest.setResult("agree");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Voting> all = votingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
    }

    @Test
    @DisplayName("결선 투표")
    void primaryVoteTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("PRIMARY");
        votingRequest.setResult("1");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Voting> all = votingRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
    }

    @Test
    @DisplayName("단선 투표 결과 검증")
    void singleVoteResultValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("SINGLE");
        votingRequest.setResult("1");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 투표 결과 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("결선 투표 결과 검증")
    void primaryVoteResultValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("PRIMARY");
        votingRequest.setResult("agree");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 투표 결과 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 종류가 맞지 않으면 오류 - 단선")
    void singleElectionTypeValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("PRIMARY");
        votingRequest.setResult("agree");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("선거 종류가 맞지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 종류가 맞지 않으면 오류 - 결선")
    void primaryElectionTypeValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("SINGLE");
        votingRequest.setResult("1");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("선거 종류가 맞지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("유권자가 이미 투표 했으면 오류 - 단선")
    void singleElectionTypeAlreadyVotedValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .election(savedElection)
                .symbol("1")
                .build();

        headquarterRepository.save(headquarter);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);
        electoralRoll.setVoted(true);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("SINGLE");
        votingRequest.setResult("agree");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message").value("이미 투표를 하였습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("유권자가 이미 투표 했으면 오류 - 결선")
    void primaryElectionTypeAlreadyVotedValidationTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.PRIMARY)
                .build();

        Election savedElection = electionRepository.save(election);

        Headquarter headquarter1 = Headquarter.builder()
                .name("test1")
                .election(savedElection)
                .symbol("1")
                .build();

        Headquarter headquarter2 = Headquarter.builder()
                .name("test2")
                .election(savedElection)
                .symbol("2")
                .build();

        headquarterRepository.save(headquarter1);
        headquarterRepository.save(headquarter2);

        Member voter = new Member(USER_ID, USER_NAME);

        ElectoralRoll electoralRoll = new ElectoralRoll();
        electoralRoll.setElection(election);
        electoralRoll.setMember(voter);
        electoralRoll.setVoted(true);

        electoralRollRepository.save(electoralRoll);

        VotingRequest votingRequest = new VotingRequest();
        votingRequest.setElectionId(savedElection.getId());
        votingRequest.setElectionType("PRIMARY");
        votingRequest.setResult("1");

        // expected
        mockMvc.perform(post("/api/voting")
                        .contentType(APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(votingRequest))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(jsonPath("$.message").value("이미 투표를 하였습니다."))
                .andDo(print());
    }
}