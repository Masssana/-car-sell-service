package ru.college.carmarketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.college.carmarketplace.model.requests.PhoneNumberRequest;
import ru.college.carmarketplace.model.requests.UserUpdateRequest;
import ru.college.carmarketplace.service.impl.UserServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userService;

    @PutMapping("/update")
    public void update(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        userService.updateUser(userUpdateRequest, request);
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpServletRequest request) {
        return userService.isExpired(request);
    }

    @PostMapping("/phone/add")
    public ResponseEntity<String> setPhoneNumber(@RequestBody PhoneNumberRequest numberRequest, HttpServletRequest request) {
        return userService.setPhoneNumber(numberRequest, request);
    }
}
