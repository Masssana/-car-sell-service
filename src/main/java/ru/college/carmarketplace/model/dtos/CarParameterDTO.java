package ru.college.carmarketplace.model.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarParameterDTO {
    private Long id;
    private String image;
    private String title;
    private boolean available;
}