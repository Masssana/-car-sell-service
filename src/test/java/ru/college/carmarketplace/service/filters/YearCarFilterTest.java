package ru.college.carmarketplace.service.filters;

import org.junit.jupiter.api.Test;
import ru.college.carmarketplace.model.dtos.CarFilter;

import static org.assertj.core.api.Assertions.assertThat;

class YearCarFilterTest {

    private final YearCarFilter yearCarFilter = new YearCarFilter();

    @Test
    void toSpecification_alwaysReturnsNonNull() {
        CarFilter filter = new CarFilter();
        filter.setYearFrom(2018);
        filter.setYearTo(2022);

        assertThat(yearCarFilter.toSpecification(filter)).isNotNull();
    }

    @Test
    void toSpecification_withOnlyYearFrom_returnsSpec() {
        CarFilter filter = new CarFilter();
        filter.setYearFrom(2020);
        filter.setYearTo(null);

        assertThat(yearCarFilter.toSpecification(filter)).isNotNull();
    }
}
