package ru.college.carmarketplace.service.filters;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.service.CarSpecification;

@Service
public class SearchCarFilter implements CarSpecification {
    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if(filter.getSearch() == null) return null;

        return ((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + filter.getSearch().toLowerCase() + "%"));
    }
}
