package ru.college.carmarketplace.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueSuggestion {
    private String brand;
    private String model;
    private String text;
}
