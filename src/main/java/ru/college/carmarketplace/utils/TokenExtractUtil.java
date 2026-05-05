package ru.college.carmarketplace.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.service.JwtService;

public class TokenExtractUtil {

    public static Integer extractIdFromToken(HttpServletRequest request, JwtService jwtService){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный заголовок авторизации");
        }

        String token = authHeader.substring(7);

        return jwtService.extractUserId(token);
    }
}
