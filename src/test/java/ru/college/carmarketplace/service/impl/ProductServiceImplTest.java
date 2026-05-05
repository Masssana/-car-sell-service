package ru.college.carmarketplace.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.model.entities.SvgImages;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.repo.SvgImagesRepository;
import ru.college.carmarketplace.service.CarSpecificationBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarSpecificationBuilder specificationBuilder;
    @Mock
    private SvgImagesRepository imagesRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProduct_missingId_returnsNull() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(productService.getProduct(99L)).isNull();
    }

    @Test
    void getProduct_found_mapsToDto() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setPrice(BigDecimal.valueOf(1_500_000));
        car.setBooked(false);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarDTO dto = productService.getProduct(1L);

        assertThat(dto.getBrand()).isEqualTo("Toyota");
        assertThat(dto.getModel()).isEqualTo("Camry");
    }

    @Test
    void getFilterParameters_loadsBrandsAndRanges() {
        when(carRepository.findAllBrands()).thenReturn(List.of("BMW", "Audi"));

        Map<String, Object> map = productService.getFilterParameters(null);

        assertThat(map.get("brands")).isEqualTo(List.of("BMW", "Audi"));
        assertThat(map.get("year")).isEqualTo(Map.of("min", 0, "max", 2026));
        assertThat(map).doesNotContainKey("models");
    }

    @Test
    void getFilterParameters_withBrands_includesModels() {
        when(carRepository.findAllBrands()).thenReturn(List.of("BMW"));
        when(carRepository.findModelsByBrand("BMW")).thenReturn(List.of("X5", "X3"));

        Map<String, Object> map = productService.getFilterParameters(new String[]{"BMW"});

        assertThat(map.get("models")).isEqualTo(List.of("X5", "X3"));
    }

    @Test
    void getCarsByParams_appliesNotBookedPredicate() {
        CarFilter filter = new CarFilter();
        var pageable = PageRequest.of(0, 5);
        Specification<Car> base = (root, query, cb) -> cb.conjunction();
        when(specificationBuilder.build(filter)).thenReturn(base);

        Car car = new Car();
        car.setId(2L);
        car.setBrand("Lada");
        car.setModel("Vesta");
        car.setPrice(BigDecimal.TEN);
        car.setBooked(false);

        when(carRepository.findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(car)));

        var page = productService.getCarsByParams(filter, pageable);

        assertThat(page.getContent()).hasSize(1);
        verify(carRepository).findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(pageable));
    }

    @Test
    void getSvgImage_returnsStoredData() {
        SvgImages svg = new SvgImages();
        svg.setName("logo");
        svg.setData("<svg></svg>");
        when(imagesRepository.findDataByName("logo")).thenReturn(svg);

        assertThat(productService.getSvgImage("logo")).isEqualTo("<svg></svg>");
    }
}
