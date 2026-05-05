package ru.college.carmarketplace.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarDataCreateRequest {
    String title;
    String brand;
    String model;
    String price;
    String engineType;
    String transmission;
    String driveType;
    String bodyType;
    String year;
    String engineVolume;
    String capacity;
    String mileage;
    String exclusives;
    String recommends;
    String location;
    String parametersJson;
    MultipartFile[] mainFiles;
    MultipartFile[] exclusiveFiles;
    MultipartFile[] recommendFiles;
}
