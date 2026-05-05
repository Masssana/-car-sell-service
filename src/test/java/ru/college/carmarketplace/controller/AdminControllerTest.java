package ru.college.carmarketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.service.AdminService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void getListingOptions_returnsMap() throws Exception {
        when(adminService.getListingOptions())
                .thenReturn(ResponseEntity.ok(Map.of("brands", List.of("BMW"))));

        mockMvc.perform(get("/api/listing/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brands[0]").value("BMW"));
    }

    @Test
    void getAllCars_returnsPage() throws Exception {
        CarDTO dto = CarDTO.builder().id(9L).brand("VW").price(BigDecimal.TEN).build();
        Pageable pageable = PageRequest.of(0, 10);
        when(adminService.getAllCars(eq(100L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

        mockMvc.perform(get("/api/listing").param("search", "100").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(9));
    }

    @Test
    void getCarById_returnsPayload() throws Exception {
        when(adminService.getCarById(2L)).thenReturn(Map.of("car", "data"));

        mockMvc.perform(get("/api/listing/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.car").value("data"));
    }
}
