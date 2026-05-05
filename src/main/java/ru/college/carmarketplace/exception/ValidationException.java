package ru.college.carmarketplace.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class ValidationException extends RuntimeException {
    private Map<String, String> errors;
    public ValidationException(String message) {
        super(message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
