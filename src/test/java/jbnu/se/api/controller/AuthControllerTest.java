package jbnu.se.api.controller;

import jbnu.se.api.util.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static jbnu.se.api.config.SampleAuthInfo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("로그인 성공")
    void successfulLoginTest() throws Exception {
        // expected
        mockMvc.perform(post("/api/login")
                        .param("username", USER_ID)
                        .param("password", USER_PW)
                        .contentType(APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공시 헤더에 jwt 토큰을 응답한다.")
    void headerContainsJwtTest() throws Exception {
        // given
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .param("username", USER_ID)
                        .param("password", USER_PW)
                        .contentType(APPLICATION_FORM_URLENCODED))
                .andReturn();

        // when
        String header = mvcResult.getResponse().getHeader(AUTHORIZATION);

        // then
        assertThat(header).isNotNull()
                .startsWith("Bearer ");
    }

    @Test
    @DisplayName("로그인 성공시 응답받은 jwt 토큰에는 유저의 정보가 들어있다. - USER")
    void jwtTokenContainUserInfoTest() throws Exception {
        // given
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .param("username", USER_ID)
                        .param("password", USER_PW)
                        .contentType(APPLICATION_FORM_URLENCODED))
                .andReturn();

        // when
        String header = mvcResult.getResponse().getHeader(AUTHORIZATION);
        assert header != null;
        String token = header.substring(7);
        String userIdFromToken = jwtUtils.getUserIdFromToken(token);
        String usernameFromToken = jwtUtils.getUsernameFromToken(token);
        String userRoleFromToken = jwtUtils.getUserRoleFromToken(token);

        // then
        assertThat(userIdFromToken).isEqualTo(USER_ID);
        assertThat(usernameFromToken).isEqualTo(USER_NAME);
        assertThat(userRoleFromToken).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("로그인 성공시 응답받은 jwt 토큰에는 유저의 정보가 들어있다. - ADMIN")
    void jwtTokenContainAdminInfoTest() throws Exception {
        // given
        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .param("username", ADMIN_ID)
                        .param("password", ADMIN_PW)
                        .contentType(APPLICATION_FORM_URLENCODED))
                .andReturn();

        // when
        String header = mvcResult.getResponse().getHeader(AUTHORIZATION);
        assert header != null;
        String token = header.substring(7);
        String userIdFromToken = jwtUtils.getUserIdFromToken(token);
        String usernameFromToken = jwtUtils.getUsernameFromToken(token);
        String userRoleFromToken = jwtUtils.getUserRoleFromToken(token);

        // then
        assertThat(userIdFromToken).isEqualTo(ADMIN_ID);
        assertThat(usernameFromToken).isEqualTo(ADMIN_NAME);
        assertThat(userRoleFromToken).isEqualTo("ROLE_ADMIN");
    }


    @Test
    @DisplayName("아이디 또는 비밀번호가 잘못된 경우 에러를 반환한다.")
    void unsuccessfulLogin() throws Exception {
        // expected
        mockMvc.perform(post("/api/login")
                        .param("username", USER_ID)
                        .param("password", USER_PW + "error")
                        .contentType(APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andDo(print());
    }
}