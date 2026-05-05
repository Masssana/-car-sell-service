package ru.college.carmarketplace.service.filters;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.CarSpecification;
import ru.college.carmarketplace.model.entities.Car;

@Service
public class PriceCarFilter implements CarSpecification {
    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if (filter.getPriceFrom() == null || filter.getPriceTo() == null) {
            return null;
        }
        return (root, query, cb) ->
            cb.between(root.get("price"), filter.getPriceFrom(), filter.getPriceTo());
    }
}
