package jbnu.se.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.request.ElectionRequest;
import jbnu.se.api.response.ElectionResponse;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.of;
import static jbnu.se.api.domain.ElectionType.SINGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private ElectionRepository electionRepository;

    @AfterEach
    void tearDown() {
        electionRepository.deleteAll();
    }

    @Test
    @DisplayName("관리자 권한이 없으면 에러 반환")
    void roleTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        LocalDateTime startDate = of(2100, 1, 1, 1, 1);
        LocalDateTime endDate = startDate.plusDays(1);
        electionRequest.setPeriod(new ElectionRequest.Period());
        electionRequest.getPeriod().setStartDate(startDate);
        electionRequest.getPeriod().setEndDate(endDate);
        electionRequest.setElectionType(SINGLE.name());

        mockMvc.perform(post("/api/elections")
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자가 선거를 정상적으로 등록한다.")
    void registerElectionTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        LocalDateTime startDate = of(2100, 1, 1, 0, 0);
        LocalDateTime endDate = startDate.plusDays(1);
        electionRequest.setPeriod(new ElectionRequest.Period());
        electionRequest.getPeriod().setStartDate(startDate);
        electionRequest.getPeriod().setEndDate(endDate);
        electionRequest.setElectionType(SINGLE.name());

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
        assertThat(election.getPeriod().getStartDate()).isEqualTo(electionRequest.getPeriod().getStartDate());
        assertThat(election.getPeriod().getEndDate()).isEqualTo(electionRequest.getPeriod().getEndDate());
    }

    @Test
    @DisplayName("등록된 선거를 가져온다")
    void getAllElectionsTest() throws Exception {
        Election election = Election.builder()
                .title("test")
                .electionType(SINGLE)
                .period(new Period(of(2100, 1, 1, 0, 0), of(2100, 1, 2, 0, 0)))
                .build();

        electionRepository.save(election);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/elections")
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ElectionResponse> elections = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        // then
        assertThat(elections).hasSize(1);
    }

    @Test
    @DisplayName("선거 개설 요청시 값이 존재해야 한다.")
    void validRequestTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        String json = objectMapper.writeValueAsString(electionRequest);

        // expected
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.period").value("선거 기간을 입력해 주세요."))
                .andExpect(jsonPath("$.validation.electionType").value("선거 종류를 입력해 주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 개설 요청시 선거 종류는 'SINGLE' 또는 'PRIMARY'이어야 한다.")
    void validElectionTypeTest() throws Exception {
        // given
        String request = "{\"title\":\"test\",\"period\":{\"startDate\": \"2100-01-01T00:00:00\", \"endDate\": \"2100-01-02T00:00:00\"},\"electionType\":\"ERROR\"}";

        // expected
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.electionType").value("유효하지 않은 선거 종류 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 개설 요청시 선거 시작과 종료 날짜가 존재해야 한다.")
    void validElectionDateTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        electionRequest.setElectionType("SINGLE");
        electionRequest.setPeriod(new ElectionRequest.Period());

        // expected
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation['period.startDate']").value("선거 시작 날짜를 입력해 주세요."))
                .andExpect(jsonPath("$.validation['period.endDate']").value("선거 종료 날짜를 입력해 주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 개설 요청시 선거 시작 날짜와 종료 날짜는 과거일 수 없다.")
    void validElectionPeriodTest() throws Exception {
        //given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        electionRequest.setElectionType("SINGLE");
        electionRequest.setPeriod(new ElectionRequest.Period());
        electionRequest.getPeriod().setStartDate(of(2000, 1, 1, 0, 0, 0));
        electionRequest.getPeriod().setEndDate(of(2000, 1, 2, 0, 0, 0));

        // expected
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation['period.startDate']").value("과거 날짜로는 설정 할 수 없습니다."))
                .andExpect(jsonPath("$.validation['period.endDate']").value("과거 날짜로는 설정 할 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("선거 시작날짜는 종료날짜 이전이어야한다.")
    void electionDateRangeTest() throws Exception {
        // given
        ElectionRequest electionRequest = new ElectionRequest();
        electionRequest.setTitle("test");
        electionRequest.setElectionType("SINGLE");
        electionRequest.setPeriod(new ElectionRequest.Period());
        electionRequest.getPeriod().setStartDate(of(2100, 2, 1, 0, 0, 0));
        electionRequest.getPeriod().setEndDate(of(2100, 1, 1, 0, 0, 0));

        // expected
        mockMvc.perform(post("/api/elections")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.period").value("선거 시작 날짜는 종료 날짜보다 앞으로 설정해 주세요."))
                .andDo(print());
    }
}