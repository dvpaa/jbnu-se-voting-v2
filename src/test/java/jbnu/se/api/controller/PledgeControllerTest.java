package jbnu.se.api.controller;

import jbnu.se.api.config.JwtAdminRequestPostProcessor;
import jbnu.se.api.config.JwtUserRequestPostProcessor;
import jbnu.se.api.domain.Headquarter;
import jbnu.se.api.domain.Pledge;
import jbnu.se.api.repository.HeadquarterRepository;
import jbnu.se.api.repository.PledgeRepository;
import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

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
    @DisplayName("공약 등록에는 관리자 권한이 있어야 한다.")
    void registerPledgeRoleTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        mockMvc.perform(post("/api/admin/pledges")
                        .with(JwtUserRequestPostProcessor.jwtUser(jwtUtils))
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                        .param("headquarterId", String.valueOf(savedHeadquarter.getId()))
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("관리자는 공약을 선본에 등록할 수 있다.")
    void registerPledgeTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);

        // expected
        mockMvc.perform(post("/api/admin/pledges")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                        .param("headquarterId", String.valueOf(savedHeadquarter.getId()))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        List<Pledge> all = pledgeRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getDescription()).isEqualTo("test");
    }

    @Test
    @DisplayName("공약 등록 요청시 선본 id가 있어야 한다..")
    void registerPledgeValidationTest() throws Exception {
        // given
        Headquarter headquarter = Headquarter.builder()
                .name("test")
                .build();

        // expected
        mockMvc.perform(post("/api/admin/pledges")
                        .with(JwtAdminRequestPostProcessor.jwtAdmin(jwtUtils))
                        .contentType(MULTIPART_FORM_DATA_VALUE)
                        .param("description", "test")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.validation.headquarterId").value("선본 id를 입력해 주세요."))
                .andDo(print());
    }
}