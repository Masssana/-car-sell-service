package ru.college.carmarketplace.service.filters;

import org.junit.jupiter.api.Test;
import ru.college.carmarketplace.model.dtos.CarFilter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrandCarFilterTest {

    private final BrandCarFilter filter = new BrandCarFilter();

    @Test
    void toSpecification_nullBrands_returnsNull() {
        CarFilter carFilter = new CarFilter();
        carFilter.setBrands(null);

        assertThat(filter.toSpecification(carFilter)).isNull();
    }

    @Test
    void toSpecification_withBrands_returnsSpecification() {
        CarFilter carFilter = new CarFilter();
        carFilter.setBrands(List.of("BMW", "Audi"));

        assertThat(filter.toSpecification(carFilter)).isNotNull();
    }
}
