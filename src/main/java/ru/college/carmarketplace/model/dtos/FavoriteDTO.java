package ru.college.carmarketplace.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long carId;
    private List<CarImageDTO> images;
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
}