package ru.college.carmarketplace.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.college.carmarketplace.model.entities.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    List<Car> findTop3ByModelContainingIgnoreCase(String model);
    @Query("SELECT DISTINCT c.brand FROM Car c")
    List<String> findAllBrands();

    @Query("SELECT DISTINCT c.model FROM Car c WHERE c.brand = :brand")
    List<String> findModelsByBrand(@Param("brand") String brand);

    Page<Car> findById(Long id, Pageable pageable);
}
