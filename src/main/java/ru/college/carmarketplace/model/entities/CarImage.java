package ru.college.carmarketplace.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_type", nullable = false) // "main", "exclusives", "recommends"
    private String imageType;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}