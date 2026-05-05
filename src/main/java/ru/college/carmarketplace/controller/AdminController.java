package ru.college.carmarketplace.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.college.carmarketplace.annotations.AdminOnly;
import ru.college.carmarketplace.model.requests.CarDataCreateRequest;
import ru.college.carmarketplace.model.dtos.*;
import ru.college.carmarketplace.model.requests.CarDataUpdateRequest;
import ru.college.carmarketplace.service.AdminService;
import ru.college.carmarketplace.model.entities.Car;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/listing")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/create")
    @AdminOnly
    public ResponseEntity<Car> createProduct(@ModelAttribute CarDTO product, @RequestPart("mainImages") MultipartFile[] mainImages,
                                             @RequestPart(value = "mainImages", required = false) MultipartFile[] exclusiveImages,
                                             @RequestPart(value = "recommendImages", required = false) MultipartFile[] recommendImages) throws IOException {

       return adminService.create(product, mainImages, exclusiveImages, recommendImages);
    }

    @PostMapping("/delete/{id}")
    @AdminOnly
    public ResponseEntity<Car> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/options")
    @AdminOnly
    public ResponseEntity<Map<String, Object>> getListingOptions() {
        return adminService.getListingOptions();
    }

    @PostMapping
    @AdminOnly
    public ResponseEntity<?> createCar(
           @ModelAttribute CarDataCreateRequest carDataCreateRequest) {
        return adminService.tryCreatingCar(carDataCreateRequest);
    }

    @PutMapping("/{id}")
    @AdminOnly
    public ResponseEntity<?> updateCar(
            @PathVariable Long id,
            @ModelAttribute CarDataUpdateRequest updateRequest
            ) {
        return adminService.tryUpdateCar(id, updateRequest);
    }

    @GetMapping
    @AdminOnly
    public ResponseEntity<Page<CarDTO>> getAllCars(@RequestParam(required = false) Long search, Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllCars(search, pageable));
    }

    @GetMapping("/{id}")
    @AdminOnly
    public ResponseEntity<Map<String, Object>> getCarById(@PathVariable Long id) {

        return ResponseEntity.ok(adminService.getCarById(id));
    }

}

