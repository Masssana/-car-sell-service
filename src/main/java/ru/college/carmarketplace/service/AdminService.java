package ru.college.carmarketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.college.carmarketplace.model.requests.CarDataCreateRequest;
import ru.college.carmarketplace.model.dtos.CarDTO;
import ru.college.carmarketplace.model.dtos.OrderDTO;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.model.requests.CarDataUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AdminService {

    void deleteProduct(Long id);

    void updateCar(
            Long id,
            CarDTO carDTO,
            MultipartFile[] mainFiles,
            MultipartFile[] exclusiveFiles,
            MultipartFile[] recommendFiles,
            List<String> oldMain,
            List<String> oldExclusives,
            List<String> oldRecommends
    ) throws IOException;

    List<OrderDTO> getReadyOrders(String search);

    List<OrderDTO> getNewOrders(String search);

    List<OrderDTO> getNotReadyOrders(String search);

    String uploadImage(MultipartFile file);

    ResponseEntity<Car> create( CarDTO product, MultipartFile[] mainImages,
                                       MultipartFile[] exclusiveImages,
                                       MultipartFile[] recommendImages) throws IOException;

    Page<CarDTO> getAllCars(Long search, Pageable pageable);

    Map<String, Object>  getCarById(Long id);


    ResponseEntity<Map<String, Object>> getListingOptions();

    ResponseEntity<?> tryCreatingCar(CarDataCreateRequest carDataCreateRequest);

    ResponseEntity<?> tryUpdateCar(Long id,
                                   CarDataUpdateRequest updateRequest);

}
