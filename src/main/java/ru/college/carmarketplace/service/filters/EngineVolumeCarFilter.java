package ru.college.carmarketplace.service.filters;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.CarSpecification;
import ru.college.carmarketplace.model.entities.Car;

@Service
public class EngineVolumeCarFilter implements CarSpecification {

    @Override
    public Specification<Car> toSpecification(CarFilter filter) {
        if (filter.getEngineVolumeFrom() == null || filter.getEngineVolumeTo() == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.between(root.get("engineVolume"), filter.getEngineVolumeFrom(), filter.getEngineVolumeTo());
    }
}
