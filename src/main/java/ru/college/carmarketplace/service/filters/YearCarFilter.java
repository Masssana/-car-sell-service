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
public class YearCarFilter implements CarSpecification {
    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), filter.getYearFrom()));
            }

            if (filter.getYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), filter.getYearTo()));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
