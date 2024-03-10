package jbnu.se.api.controller;

import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ElectoralRollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

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
    @DisplayName("선거인 명부 등록에는 관리자 권한이 있어야한다..")
    void registerElectoralRollRoleTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        // expected
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "학번,이름\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("csv 파일의 헤더는 '학번', '이름' 이어야 한다.")
    void csvHeaderTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        // expected
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "studentId,name\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("csv 형식이 올바르지 않습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("관리자는 선거인 명부를 정상적으로 등록한다.")
    void registerElectoralRollTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        // expected
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "학번,이름\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<ElectoralRoll> all = electoralRollRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getElection().getId()).isEqualTo(savedElection.getId());
        assertThat(all.get(0).getMember().getStudentId()).isEqualTo("201911111");
        assertThat(all.get(0).getMember().getName()).isEqualTo("name1");
    }
}