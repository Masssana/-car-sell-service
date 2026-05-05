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
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.requests.FavoriteRequest;
import ru.college.carmarketplace.service.FavoriteService;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest extends ControllerTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FavoriteService favoriteService;

    @Test
    void getAllFavorites_returnsList() throws Exception {
        CarDTO dto = CarDTO.builder().id(1L).brand("Kia").price(BigDecimal.ONE).build();
        when(favoriteService.getUserFavoriteCars()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].brand").value("Kia"));
    }

    @Test
    void addFavorite_returnsMessage() throws Exception {
        when(favoriteService.addToFavorites(any(FavoriteRequest.class), any()))
                .thenReturn("Добавлено");

        mockMvc.perform(post("/api/favorite/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FavoriteRequest(10L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Добавлено"));

        verify(favoriteService).addToFavorites(any(FavoriteRequest.class), any());
    }

    @Test
    void deleteFavorite_returnsMessage() throws Exception {
        when(favoriteService.removeFromFavorites(any(FavoriteRequest.class), any()))
                .thenReturn("Удалено");

        mockMvc.perform(post("/api/favorite/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FavoriteRequest(10L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Удалено"));
    }
}
