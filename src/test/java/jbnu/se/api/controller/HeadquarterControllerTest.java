package jbnu.se.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.request.HeadquarterRequest;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HeadquarterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

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

        List<HeadquarterRequest> request = Arrays.asList(
                HeadquarterRequest.builder()
                        .electionId(saved.getId())
                        .name("test1")
                        .build(),
                HeadquarterRequest.builder()
                        .electionId(saved.getId())
                        .name("test2")
                        .build()
        );

        // expected
        mockMvc.perform(post("/api/headquarters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자는 선본을 정상적으로 등록한다.")
    void registerHeadquarterTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election saved = electionRepository.save(election);

        List<HeadquarterRequest> request = Arrays.asList(
                HeadquarterRequest.builder()
                        .electionId(saved.getId())
                        .name("test1")
                        .build(),
                HeadquarterRequest.builder()
                        .electionId(saved.getId())
                        .name("test2")
                        .build()
        );

        // expected
        mockMvc.perform(post("/api/headquarters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Headquarter> headquarters = headquarterRepository.findAll();

        assertThat(headquarters).hasSize(2);
        headquarters.forEach(headquarter -> assertThat(headquarter.getElection().getId()).isEqualTo(saved.getId()));
    }
}