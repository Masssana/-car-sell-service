package ru.college.carmarketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.model.requests.OrderRequest;
import ru.college.carmarketplace.service.AdminService;
import ru.college.carmarketplace.service.OrderService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestsController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestsControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AdminService adminService;

    @Test
    void getTracker_returnsOrders() throws Exception {
        when(orderService.tracker(any())).thenReturn(List.of(OrderDTO.builder().id(3L).build()));

        mockMvc.perform(get("/api/requests/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void getOrderById_returnsDto() throws Exception {
        when(orderService.getOrderById(5L)).thenReturn(OrderDTO.builder().id(5L).status("NEW").build());

        mockMvc.perform(get("/api/requests/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void addOrder_invokesService() throws Exception {
        OrderRequest body = new OrderRequest();
        body.setCarId(1L);
        body.setPhoneNumber("+7000");

        mockMvc.perform(post("/api/requests/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(orderService).createOrder(any(OrderRequest.class), any());
    }

    @Test
    void getActiveOrders_forAdminPath() throws Exception {
        when(adminService.getReadyOrders("q")).thenReturn(List.of(OrderDTO.builder().id(1L).build()));

        mockMvc.perform(get("/api/requests/active").param("search", "q"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(adminService).getReadyOrders("q");
    }
}
