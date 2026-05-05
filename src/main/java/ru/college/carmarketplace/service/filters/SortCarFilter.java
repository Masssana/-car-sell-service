package ru.college.carmarketplace.service.filters;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.service.CarSpecification;

@Service
public class SortCarFilter implements CarSpecification {

    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if (filter.getSort() == null) {
            return null;
        }
        return switch (filter.getSort()) {
            case "cheaper" -> (root, query, cb) -> {
                query.orderBy(cb.asc(root.get("price")));
                return cb.conjunction();
            };
            case "expensive" -> (root, query, cb) -> {
                query.orderBy(cb.desc(root.get("price")));
                return cb.conjunction();
            };
            case "mileage" -> (root, query, cb) -> {
                query.orderBy(cb.asc(root.get("mileage")));
                return cb.conjunction();
            };
            case "createdAt" -> (root, query, cb) -> {
                query.orderBy(cb.desc(root.get("createAt")));
                return cb.conjunction();
            };
            case "newer" -> (root, query, cb) -> {
                query.orderBy(cb.desc(root.get("year")));
                return cb.conjunction();
            };
            case "older" -> (root, query, cb) -> {
                query.orderBy(cb.asc(root.get("year")));
                return cb.conjunction();
            };
            default -> null;
        };
    }
}
