package ru.college.carmarketplace.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarSpecificationBuilderTest {

    @Test
    void build_allSpecsNull_returnsConjunction() {
        CarSpecification s1 = f -> null;
        CarSpecification s2 = f -> null;
        CarSpecificationBuilder builder = new CarSpecificationBuilder(List.of(s1, s2));

        Specification<Car> spec = builder.build(new CarFilter());

        assertThat(spec).isNotNull();
    }

    @Test
    void build_combinesNonNullSpecs() {
        CarSpecification alwaysTrue = f -> (root, query, cb) -> cb.conjunction();
        CarSpecification s2 = mock(CarSpecification.class);
        when(s2.toSpecification(any())).thenReturn(null);

        CarSpecificationBuilder builder = new CarSpecificationBuilder(List.of(s2, alwaysTrue));

        Specification<Car> spec = builder.build(new CarFilter());

        assertThat(spec).isNotNull();
    }
}
