package ru.college.carmarketplace.model.dtos;

import lombok.*;
import ru.college.carmarketplace.model.entities.Car;
import ru.college.carmarketplace.model.entities.CarImage;
import ru.college.carmarketplace.model.entities.CarParameter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private BigDecimal price;
    private String engineType;
    private String transmission;
    private String driveType;
    private Short capacity;
    private String bodyType;
    private Integer year;
    private Double engineVolume;
    private Integer mileage;
    private String location;
    private String title;
    private String exclusives;
    private String recommends;
    private List<CarParameterDTO> parameters;
    private LocalDateTime createAt;
    private boolean booked;


    private Map<String, List<String>> imageUrl;


    public static CarDTO getDto(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setPrice(car.getPrice());
        dto.setEngineType(car.getEngineType());
        dto.setTransmission(car.getTransmission());
        dto.setDriveType(car.getDriveType());
        dto.setCapacity(car.getCapacity());
        dto.setBodyType(car.getBodyType());
        dto.setYear(car.getYear());
        dto.setEngineVolume(car.getEngineVolume());
        dto.setMileage(car.getMileage());
        dto.setLocation(car.getLocation());
        dto.setTitle(car.getTitle());
        dto.setExclusives(car.getExclusives());
        dto.setRecommends(car.getRecommends());
        dto.setCreateAt(car.getCreateAt());
        dto.setBooked(car.isBooked());

        if (car.getImageUrl() != null) {
            dto.setImageUrl(car.getImageUrl().stream()
                    .collect(Collectors.groupingBy(
                            CarImage::getImageType,
                            Collectors.mapping(CarImage::getImageUrl, Collectors.toList())
                    )));
        }

        if (car.getParameters() != null && !car.getParameters().isEmpty()) {
            dto.setParameters(car.getParameters().stream()
                    .map(param -> new CarParameterDTO(
                            param.getId(),
                            param.getImage(),
                            param.getTitle(),
                            param.isAvailable()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
