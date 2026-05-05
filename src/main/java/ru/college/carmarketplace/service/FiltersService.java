package ru.college.carmarketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.dtos.CarDTO;

import java.util.Map;

public interface FiltersService {

    Page<CarDTO> getCarsByParams(CarFilter filter, Pageable pageable);

    Map<String, Object> getFilterParameters();
}
