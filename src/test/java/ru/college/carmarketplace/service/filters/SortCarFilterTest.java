package ru.college.carmarketplace.service.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.college.carmarketplace.model.dtos.CarFilter;

import static org.assertj.core.api.Assertions.assertThat;

class SortCarFilterTest {

    private final SortCarFilter filter = new SortCarFilter();

    @ParameterizedTest
    @ValueSource(strings = {"cheaper", "expensive", "mileage", "createdAt", "newer", "older"})
    void toSpecification_knownSort_returnsSpecification(String sort) {
        CarFilter carFilter = new CarFilter();
        carFilter.setSort(sort);

        assertThat(filter.toSpecification(carFilter)).isNotNull();
    }

    @Test
    void toSpecification_nullSort_returnsNull() {
        CarFilter carFilter = new CarFilter();
        carFilter.setSort(null);

        assertThat(filter.toSpecification(carFilter)).isNull();
    }

    @Test
    void toSpecification_unknownSort_returnsNull() {
        CarFilter carFilter = new CarFilter();
        carFilter.setSort("unknown");

        assertThat(filter.toSpecification(carFilter)).isNull();
    }
}
