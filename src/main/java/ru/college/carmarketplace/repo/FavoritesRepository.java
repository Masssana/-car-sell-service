package ru.college.carmarketplace.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.college.carmarketplace.model.entities.Favorites;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    Optional<Favorites> findByUserIdAndCarId(Integer userId, Long carId);
    boolean existsByUserIdAndCarId(Integer userId, Long carId);
}
