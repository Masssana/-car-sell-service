package ru.college.carmarketplace.service.filters;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.CarSpecification;
import ru.college.carmarketplace.model.entities.Car;

import java.util.ArrayList;
import java.util.List;

@Service
public class MileageCarFilter implements CarSpecification {
    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if (filter.getMileageFrom() == null || filter.getMileageTo() == null) {
            return null;
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter.getMileageFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("mileage"), filter.getMileageFrom()));
            }
            if (filter.getMileageTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("mileage"), filter.getMileageTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
