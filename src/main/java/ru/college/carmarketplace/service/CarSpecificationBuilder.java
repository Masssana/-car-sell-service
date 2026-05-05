package ru.college.carmarketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CarSpecificationBuilder {

    private final List<CarSpecification> specs;

    public Specification<Car> build(CarFilter filter) {
        return specs.stream()
                .map(c -> c.toSpecification(filter))
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, cb) -> cb.conjunction());
    }
}
