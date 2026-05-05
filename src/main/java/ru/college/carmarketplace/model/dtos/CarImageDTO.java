package ru.college.carmarketplace.model.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarImageDTO {
    private Long id;
    private String imageType;
    private String imageUrl;
}