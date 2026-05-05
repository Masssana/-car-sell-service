package ru.college.carmarketplace.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.service.CarSpecificationBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarSpecificationBuilder specificationBuilder;

    @InjectMocks
    private FilterServiceImpl filterService;

    @Test
    void getFilterParameters_containsExpectedKeys() {
        Map<String, Object> params = filterService.getFilterParameters();

        assertThat(params).containsKeys(
                "brands", "transmission", "engineType", "driveType",
                "capacity", "bodyType", "engineVolume", "mileage", "year", "price"
        );
        assertThat(params.get("year")).isEqualTo(Map.of("min", 0, "max", 2026));
    }

    @Test
    void getCarsByParams_delegatesToRepository() {
        CarFilter filter = new CarFilter();
        var pageable = PageRequest.of(0, 10);
        Car car = new Car();
        car.setId(1L);
        car.setBrand("BMW");
        car.setModel("X3");
        car.setPrice(BigDecimal.valueOf(1_000_000));
        car.setBooked(false);

        Specification<Car> spec = (root, query, cb) -> cb.conjunction();
        when(specificationBuilder.build(filter)).thenReturn(spec);
        when(carRepository.findAll(eq(spec), eq(pageable))).thenReturn(new PageImpl<>(List.of(car)));

        Page<CarDTO> page = filterService.getCarsByParams(filter, pageable);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getBrand()).isEqualTo("BMW");
        verify(carRepository).findAll(eq(spec), eq(pageable));
    }
}
