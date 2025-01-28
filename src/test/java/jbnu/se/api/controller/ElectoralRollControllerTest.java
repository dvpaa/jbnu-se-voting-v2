package jbnu.se.api.controller;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import jbnu.se.api.domain.Election;
import jbnu.se.api.domain.ElectionType;
import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Period;
import jbnu.se.api.repository.ElectionRepository;
import jbnu.se.api.repository.ElectoralRollRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ElectoralRollControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    @WithMockUser(username = "name")
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
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
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
                "text/csv",
                "studentId,name\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("csv 형식이 올바르지 않습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
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
                "text/csv",
                "학번,이름\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<ElectoralRoll> all = electoralRollRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getElection().getId()).isEqualTo(savedElection.getId());
        assertThat(all.getFirst().getMember().getStudentId()).isEqualTo("201911111");
        assertThat(all.getFirst().getMember().getName()).isEqualTo("name1");
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("파일은 null이 될 수 없다.")
    void fileNullValidateTest() throws Exception {
        // given
        Election election = Election.builder()
                .title("test")
                .period(new Period(of(2100, 1, 1, 0, 0),
                        of(2100, 1, 2, 0, 0)))
                .electionType(ElectionType.SINGLE)
                .build();

        Election savedElection = electionRepository.save(election);

        // expected
        mockMvc.perform(post("/api/admin/electoralRoll")
                        .contentType(MULTIPART_FORM_DATA)
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.file").value("명부 파일을 업로드 해주세요."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("csv 파일만 업로드 할 수 있다.")
    void csvValidateTest() throws Exception {
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
                "test.exe",
                "exe",
                "학번,이름\n201911111,name1".getBytes()
        );

        mockMvc.perform(multipart("/api/admin/electoralRoll")
                        .file(file)
                        .param("electionId", String.valueOf(savedElection.getId()))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.file").value("명부 파일은 csv 이어야 합니다."))
                .andDo(print());
    }
}