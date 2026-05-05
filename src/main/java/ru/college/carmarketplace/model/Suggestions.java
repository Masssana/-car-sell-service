package ru.college.carmarketplace.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suggestions {
    private String imageUrl;
    private String type;
    private ValueSuggestion value;
}
