package ru.college.carmarketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.enums.Role;
import ru.college.carmarketplace.model.requests.AuthenticationRequest;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.model.responses.AuthenticationResponse;
import ru.college.carmarketplace.model.responses.ResetResponse;
import ru.college.carmarketplace.service.impl.AuthenticationServiceImpl;
import ru.college.carmarketplace.service.impl.LogoutServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationServiceImpl service;

    @MockitoBean
    private LogoutServiceImpl logoutServiceImpl;

    @Test
    void register_callsServiceAndReturnsOk() throws Exception {
        RegisterRequest body = RegisterRequest.builder()
                .name("Иван")
                .email("a@b.ru")
                .password("password123")
                .build();
        doNothing().when(service).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString());

        verify(service).register(any(RegisterRequest.class));
    }

    @Test
    void login_returnsTokensAndUserFields() throws Exception {
        AuthenticationRequest body = AuthenticationRequest.builder()
                .email("user@test.ru")
                .password("secret123")
                .build();
        AuthenticationResponse svcResponse = AuthenticationResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .id(5)
                .name("User")
                .email("user@test.ru")
                .role(Role.USER)
                .phoneNumber("+7")
                .build();
        when(service.authenticate(any(AuthenticationRequest.class), any()))
                .thenReturn(svcResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.email").value("user@test.ru"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void verifyEmail_success() throws Exception {
        RegisterRequest body = RegisterRequest.builder()
                .email("a@b.ru")
                .confirmCode("123456")
                .type("confirmEmail")
                .build();
        when(service.verify(any(RegisterRequest.class))).thenReturn(true);

        mockMvc.perform(post("/auth/verify/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void passwordReset_returnsMessage() throws Exception {
        AuthenticationRequest body = AuthenticationRequest.builder()
                .email("a@b.ru")
                .password("unused")
                .build();
        when(service.reset(any(AuthenticationRequest.class)))
                .thenReturn(new ResetResponse("Код отправлен"));

        mockMvc.perform(post("/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Код отправлен"));
    }

    @Test
    void isExpired_delegatesToLogoutService() throws Exception {
        when(logoutServiceImpl.isAccessExpired("Bearer x")).thenReturn("Токен действителен");

        mockMvc.perform(get("/auth/is/expired").header("Authorization", "Bearer x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Токен действителен"));
    }
}
