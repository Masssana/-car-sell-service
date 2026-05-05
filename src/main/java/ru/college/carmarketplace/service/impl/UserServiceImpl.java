package ru.college.carmarketplace.service.impl;


import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.exception.CustomException;
import ru.college.carmarketplace.model.requests.PhoneNumberRequest;
import ru.college.carmarketplace.model.requests.UserUpdateRequest;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.service.JwtService;
import ru.college.carmarketplace.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private static final int TOKEN_INDEX = 7;

    @Override
    public void updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        AppUser user = userRepository.findById(getCurrentUserId(request)).orElseThrow(() -> new CustomException("Такого юзера не существует "));
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setName(userUpdateRequest.getName());
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<String> isAccessExpired() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getCredentials() == null) {
            throw new CustomException("Токен отсутствует");
        }

        String token = authentication.getCredentials().toString();

        boolean isExpired = jwtService.isTokenExpired(token);

        return isExpired ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен недействителен") : ResponseEntity.ok("Токен действителен");
    }

    public ResponseEntity<String> isExpired(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Токен недействителен либо отсутсвует");
        }


        String token = authHeader.substring(TOKEN_INDEX);
        boolean isExpired = jwtService.isTokenExpired(token);

        return isExpired ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен протух") : ResponseEntity.ok(token);

    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный заголовок авторизации");
        }

        String token = authHeader.substring(TOKEN_INDEX);
        Integer userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный токен: ID пользователя не найден");
        }
        return userId;
    }

    public ResponseEntity<String> setPhoneNumber(@RequestBody PhoneNumberRequest numberRequest, HttpServletRequest request) {
        AppUser user = userRepository.findById(getCurrentUserId(request)).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Юзер не найден"));

        user.setPhoneNumber(numberRequest.getPhone());
        userRepository.save(user);
        return ResponseEntity.ok("Номер добавлен");

    }
}
