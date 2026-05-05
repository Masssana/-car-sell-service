package ru.college.carmarketplace.service.filters;

import org.junit.jupiter.api.Test;
import ru.college.carmarketplace.model.dtos.CarFilter;

import static org.assertj.core.api.Assertions.assertThat;

class SearchCarFilterTest {

    private final SearchCarFilter filter = new SearchCarFilter();

    @Test
    void toSpecification_nullSearch_returnsNull() {
        CarFilter carFilter = new CarFilter();
        carFilter.setSearch(null);

        assertThat(filter.toSpecification(carFilter)).isNull();
    }

    @Test
    void toSpecification_withSearch_returnsSpecification() {
        CarFilter carFilter = new CarFilter();
        carFilter.setSearch("Camry");

        assertThat(filter.toSpecification(carFilter)).isNotNull();
    }
}
