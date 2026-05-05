package ru.college.carmarketplace.service.filters;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.CarSpecification;
import ru.college.carmarketplace.model.entities.Car;

@Service
public class CarTypeFilter implements CarSpecification {
    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if (filter.getCarType() != null) {
            switch (filter.getCarType().toLowerCase()) {
                case "new":
                    return (root, query, cb) -> cb.equal(root.get("mileage"), 0);
                case "used":
                    return (root, query, cb) -> cb.gt(root.get("mileage"), 0);

            }
        }
        return null;
    }
}
