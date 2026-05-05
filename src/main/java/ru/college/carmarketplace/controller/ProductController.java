package ru.college.carmarketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.college.carmarketplace.model.dtos.CarFilter;
import ru.college.carmarketplace.model.Suggestions;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.repo.SvgImagesRepository;
import ru.college.carmarketplace.service.ProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final SvgImagesRepository svgImagesRepository;
    private final ProductService productService;

    @GetMapping("/car/{id}")
    public ResponseEntity<CarDTO> getProduct(@PathVariable Long id) {
        CarDTO product = productService.getProduct(id);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }


    @GetMapping("/brands")
    public ResponseEntity<List<String>> getBrands() {
        return ResponseEntity.ok(productService.getBrands());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Suggestions>> searchBar(@RequestParam String query){
        return ResponseEntity.ok(productService.searchSuggestions(query));
    }

    @GetMapping("/catalog")
    public Page<CarDTO> getCars(CarFilter carFilter, Pageable pageable) {
        Pageable adjustedPageable = PageRequest.of(
                pageable.getPageNumber() - 1,
                9
        );

        return productService.getCarsByParams(carFilter, adjustedPageable);
    }

    @GetMapping("brandsRequest")
    public ResponseEntity<Map<String, Object>> getFiltersRequest(@RequestParam(required = false) String[] brand){
        return ResponseEntity.ok(productService.getFilterParameters(brand));
    }

    @GetMapping("/icon/{name}")
    public ResponseEntity<String> getIcon(@PathVariable String name){
        return ResponseEntity.ok(productService.getSvgImage(name));
    }

}
