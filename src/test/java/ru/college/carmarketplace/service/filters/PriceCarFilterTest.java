package ru.college.carmarketplace.service.filters;

import org.junit.jupiter.api.Test;
import ru.college.carmarketplace.model.dtos.CarFilter;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCarFilterTest {

    private final PriceCarFilter priceCarFilter = new PriceCarFilter();

    @Test
    void toSpecification_nullBounds_returnsNull() throws Exception {
        CarFilter filter = new CarFilter();
        var priceFrom = CarFilter.class.getDeclaredField("priceFrom");
        priceFrom.setAccessible(true);
        priceFrom.set(filter, null);
        var priceTo = CarFilter.class.getDeclaredField("priceTo");
        priceTo.setAccessible(true);
        priceTo.set(filter, null);

        assertThat(priceCarFilter.toSpecification(filter)).isNull();
    }

    @Test
    void toSpecification_withBounds_returnsSpec() {
        CarFilter filter = new CarFilter();
        filter.setPriceFrom(BigDecimal.ONE);
        filter.setPriceTo(BigDecimal.TEN);

        assertThat(priceCarFilter.toSpecification(filter)).isNotNull();
    }
}
