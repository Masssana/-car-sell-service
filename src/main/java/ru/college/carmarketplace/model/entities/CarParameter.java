package ru.college.carmarketplace.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car_parameters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    private String image;
    private String title;
    private boolean available;
}