package ru.college.carmarketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.college.carmarketplace.model.requests.FavoriteRequest;
import ru.college.carmarketplace.model.dtos.CarDTO;

import java.util.List;

public interface FavoriteService {
    String addToFavorites(FavoriteRequest favoriteRequest, HttpServletRequest request);
    String removeFromFavorites(FavoriteRequest favoriteRequest, HttpServletRequest request);
    List<CarDTO> getUserFavoriteCars();
}
