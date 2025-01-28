package jbnu.se.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Pledge;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.PledgeRepository;
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
class PledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    @WithMockUser(username = "name")
    @DisplayName("공약 등록에는 관리자 권한이 있어야 한다.")
    void registerPledgeRoleTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        mockMvc.perform(post("/api/admin/pledges")
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                        .param("headquarterId", String.valueOf(savedHeadquarter.getId()))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("관리자는 공약을 선본에 등록할 수 있다.")
    void registerPledgeTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        // expected
        mockMvc.perform(post("/api/admin/pledges")
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                        .param("headquarterId", String.valueOf(savedHeadquarter.getId()))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Pledge> all = pledgeRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getDescription()).isEqualTo("test");
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("공약 등록 요청시 선본 id가 있어야 한다.")
    void registerPledgeValidationTest() throws Exception {
        // expected
        mockMvc.perform(post("/api/admin/pledges")
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.headquarterId").value("선본 id를 입력해 주세요."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "name", roles = {"ADMIN"})
    @DisplayName("공약 등록 요청시 이미지는 null 이거나 이미지 파일이어야 한다.")
    void imageValidationTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .symbol("1")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "exe",
                (byte[]) null
        );

        // expected
        mockMvc.perform(multipart("/api/admin/pledges")
                        .file("imageFile", file.getBytes())
                        .param("description", "test")
                        .param("headquarterId", String.valueOf(savedHeadquarter.getId()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.imageFile").value("이미지 파일이 아닙니다."))
                .andDo(print());
    }
}