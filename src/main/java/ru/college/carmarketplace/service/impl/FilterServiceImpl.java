package ru.college.carmarketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.service.CarSpecificationBuilder;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.repo.CarRepository;
import ru.college.carmarketplace.service.FiltersService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FiltersService {

    private final CarRepository carRepository;
    private final CarSpecificationBuilder specificationBuilder;

    @Override
    public Map<String, Object> getFilterParameters() {
        Map<String, Object> filters = new HashMap<>();

        filters.put("brands", Arrays.asList("BMW", "Audi", "Lada", "Mini"));

        filters.put("transmission", Arrays.asList("Механика", "Автомат", "Робот", "Вариатор"));

        filters.put("engineType", Arrays.asList("Бензин", "Дизель", "Электро", "Гибрид", "ГБО"));

        filters.put("driveType", Arrays.asList("Передний", "Задний", "Полный"));

        filters.put("capacity", Arrays.asList("2 места", "5 мест", "7 мест", "9 мест", "11 мест"));

        filters.put("bodyType", Arrays.asList(
                "Седан", "Купе", "Кроссовер", "Внедорожник",
                "Хэтчбек", "Лифтбек", "Универсал", "Фастбэк",
                "Пикап", "Минивэн", "Кабриолет", "Лимузин"
        ));

        filters.put("engineVolume", Map.of("min", 0, "max", 10));
        filters.put("mileage", Map.of("min", 0, "max", 10000000));
        filters.put("year", Map.of("min", 0, "max", 2026));
        filters.put("price", Map.of("min", 0, "max", 10000000));

        return filters;
    }

    @Override
    public Page<CarDTO> getCarsByParams(CarFilter filter, Pageable pageable) {
        var query = specificationBuilder.build(filter);
        return carRepository.findAll(query, pageable)
                .map(CarDTO::getDto);
    }
}
