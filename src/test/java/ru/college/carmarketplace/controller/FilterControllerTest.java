package ru.college.carmarketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.FiltersService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilterController.class)
@AutoConfigureMockMvc(addFilters = false)
class FilterControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FiltersService filtersService;

    @Test
    void getCars_returnsPageFromService() throws Exception {
        CarDTO dto = CarDTO.builder().id(1L).brand("Toyota").price(BigDecimal.ONE).build();
        Pageable pageable = PageRequest.of(0, 20);
        when(filtersService.getCarsByParams(any(CarFilter.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto), pageable, 1));

        mockMvc.perform(get("/api/filters/catalog").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].brand").value("Toyota"));

        verify(filtersService).getCarsByParams(any(CarFilter.class), any(Pageable.class));
    }

    @Test
    void getFiltersRequest_returnsStaticCatalog() throws Exception {
        when(filtersService.getFilterParameters())
                .thenReturn(Map.of("brands", List.of("BMW"), "year", Map.of("min", 0, "max", 2026)));

        mockMvc.perform(get("/api/filters/catalog/brandsRequest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brands[0]").value("BMW"))
                .andExpect(jsonPath("$.year.max").value(2026));
    }
}
