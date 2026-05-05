package ru.college.carmarketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.Suggestions;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.model.entities.Car;

import java.util.List;
import java.util.Map;

public interface ProductService {

    CarDTO getProduct(Long id);

    List<String> getBrands();

    List<Suggestions> searchSuggestions(String query);

    Page<CarDTO> getCarsByParams(CarFilter filter, Pageable pageable);

    Map<String, Object> getFilterParameters(String[] brands);

    public String getSvgImage(String name);
}
