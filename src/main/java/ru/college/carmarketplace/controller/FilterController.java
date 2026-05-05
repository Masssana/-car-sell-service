package ru.college.carmarketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.service.FiltersService;

import java.util.Map;

@RestController
@RequestMapping("api/filters/catalog")
@RequiredArgsConstructor
public class FilterController {
    private final FiltersService filtersService;

    @GetMapping
    public Page<CarDTO> getCars(CarFilter carFilter, Pageable pageable) {

        return filtersService.getCarsByParams(carFilter, pageable);
    }

    @GetMapping("brandsRequest")
    public ResponseEntity<Map<String, Object>> getFiltersRequest(){
        return ResponseEntity.ok(filtersService.getFilterParameters());
    }
}
