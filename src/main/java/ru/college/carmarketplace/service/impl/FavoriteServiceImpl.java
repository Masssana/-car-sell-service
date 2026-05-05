package ru.college.carmarketplace.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.model.requests.FavoriteRequest;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.model.entities.CarImage;
import ru.college.carmarketplace.model.entities.Favorites;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.repo.FavoritesRepository;
import ru.college.carmarketplace.service.FavoriteService;
import ru.college.carmarketplace.service.JwtService;
import ru.college.carmarketplace.utils.TokenExtractUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final CarRepository carRepository;
    private final FavoritesRepository favoritesRepository;
    private final JwtService jwtService;
    @Override
    public String addToFavorites(FavoriteRequest favoriteRequest, HttpServletRequest request) {
        return tryAddingToFavorite(favoriteRequest, request);
    }

    private String tryAddingToFavorite(FavoriteRequest favoriteRequest, HttpServletRequest request) {
        try {
            Integer userId = TokenExtractUtil.extractIdFromToken(request, jwtService);
            String carExistence = checkCarExistenceInFavorites(favoriteRequest, userId);

            if (carExistence != null) return carExistence;

            Car car = getProduct(favoriteRequest.getId());

            Favorites favorites = new Favorites();
            favorites.setUserId(userId);
            favorites.setCar(car);
            favoritesRepository.save(favorites);

            return "Успешно добавлено";
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    private String checkCarExistenceInFavorites(FavoriteRequest favoriteRequest, Integer userId) {
        if(favoritesRepository.existsByUserIdAndCarId(userId, favoriteRequest.getId())){
            return "Машина уже в избранном";
        }
        return null;
    }

    @Override
    public String removeFromFavorites(FavoriteRequest favoriteRequest, HttpServletRequest request) {

        Integer userId = TokenExtractUtil.extractIdFromToken(request, jwtService);
        Long carId = favoriteRequest.getId();

        checkCarExistenceAndThrowException(carId);

        Optional<Favorites> favoriteOpt = favoritesRepository.findByUserIdAndCarId(userId, carId);

        if (favoriteOpt.isEmpty()) {
            return "Автомобиль не был в избранном";
        }

        favoritesRepository.delete(favoriteOpt.get());
        return "Автомобиль успешно удален из избранного";
    }

    private void checkCarExistenceAndThrowException(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Автомобиль с ID " + carId + " не найден");
        }
    }

    @Override
    public List<CarDTO> getUserFavoriteCars() {
        return favoritesRepository.findAll().stream()
                .filter(favorite -> favorite.getCar() != null)
                .map(favorite -> {
                    Car car = favorite.getCar();
                    CarDTO dto = CarDTO.getDto(car);

                    if (car.getImageUrl() != null) {
                        dto.setImageUrl(car.getImageUrl().stream()
                                .collect(Collectors.groupingBy(
                                        CarImage::getImageType,
                                        Collectors.mapping(CarImage::getImageUrl, Collectors.toList())
                                )));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Car getProduct(Long id) {
        return carRepository.findById(id).orElse(null);
    }
}
