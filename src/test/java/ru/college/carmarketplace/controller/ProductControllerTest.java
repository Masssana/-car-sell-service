package ru.college.carmarketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.college.carmarketplace.model.Suggestions;
import ru.college.carmarketplace.model.ValueSuggestion;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.controller.support.ControllerTestSupport;
import ru.college.carmarketplace.repo.SvgImagesRepository;
import ru.college.carmarketplace.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private SvgImagesRepository svgImagesRepository;

    @Test
    void getProduct_returnsOkAndBody() throws Exception {
        CarDTO dto = CarDTO.builder()
                .id(1L)
                .brand("BMW")
                .model("X5")
                .price(BigDecimal.valueOf(3_000_000))
                .build();
        when(productService.getProduct(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brand").value("BMW"))
                .andExpect(jsonPath("$.model").value("X5"));
    }

    @Test
    void getBrands_returnsList() throws Exception {
        when(productService.getBrands()).thenReturn(List.of("Audi", "BMW"));

        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("Audi"));
    }

    @Test
    void searchBar_passesQuery() throws Exception {
        List<Suggestions> suggestions = List.of(
                new Suggestions("u", "text", new ValueSuggestion(null, null, "camry"))
        );
        when(productService.searchSuggestions("camry")).thenReturn(suggestions);

        mockMvc.perform(get("/api/search").param("query", "camry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("text"));
    }

    @Test
    void getCatalog_adjustsPageAndPageSize() throws Exception {
        CarDTO dto = CarDTO.builder().id(2L).brand("Lada").model("Vesta").price(BigDecimal.TEN).build();
        when(productService.getCarsByParams(any(), eq(PageRequest.of(0, 9))))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 9), 1));

        mockMvc.perform(get("/api/catalog").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].brand").value("Lada"));

        verify(productService).getCarsByParams(any(), eq(PageRequest.of(0, 9)));
    }

    @Test
    void getFiltersRequest_delegatesToService() throws Exception {
        when(productService.getFilterParameters(new String[]{"BMW"}))
                .thenReturn(Map.of("brands", List.of("BMW"), "models", List.of("X3")));

        mockMvc.perform(get("/api/brandsRequest").param("brand", "BMW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brands[0]").value("BMW"))
                .andExpect(jsonPath("$.models[0]").value("X3"));
    }

    @Test
    void getIcon_returnsSvgPayload() throws Exception {
        when(productService.getSvgImage("logo")).thenReturn("<svg/>");

        mockMvc.perform(get("/api/icon/logo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("<svg/>"));
    }
}
