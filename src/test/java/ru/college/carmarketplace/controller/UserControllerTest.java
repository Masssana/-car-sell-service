package ru.college.carmarketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.model.requests.PhoneNumberRequest;
import ru.college.carmarketplace.model.requests.UserUpdateRequest;
import ru.college.carmarketplace.service.impl.UserServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;

    @Test
    void update_delegatesToService() throws Exception {
        UserUpdateRequest body = new UserUpdateRequest();
        body.setPhoneNumber("+7999");

        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(userService).updateUser(any(UserUpdateRequest.class), any());
    }

    @Test
    void check_returnsServiceResponse() throws Exception {
        when(userService.isExpired(any())).thenReturn(ResponseEntity.ok("jwt-token"));

        mockMvc.perform(get("/api/user/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("jwt-token"));
    }

    @Test
    void setPhoneNumber_returnsOk() throws Exception {
        PhoneNumberRequest body = new PhoneNumberRequest();
        body.setPhone("+7888");
        when(userService.setPhoneNumber(any(PhoneNumberRequest.class), any()))
                .thenReturn(ResponseEntity.ok("Сохранено"));

        mockMvc.perform(post("/api/user/phone/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Сохранено"));
    }
}
