package ru.college.carmarketplace.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id")
    private List<CarImage> imageUrl;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "engineType")
    private String engineType;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "driveType")
    private String driveType;

    @Column(name = "capacity")
    private Short capacity;

    @Column(name = "bodyType")
    private String bodyType;

    @Column(name = "year")
    private Integer year;

    @Column(name = "engineVolume")
    private Double engineVolume;

    @Column(name = "mileage")
    private Integer mileage;

    @Column
    private String location;

    @Column
    private LocalDateTime createAt;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String exclusives;

    @Column(columnDefinition = "TEXT")
    private String recommends;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id")
    private List<CarParameter> parameters;

    @Column(name = "booked", nullable = false)
    private boolean booked = false;

    @PrePersist
    private void onCreate() {
        this.createAt = LocalDateTime.now();
    }


}
