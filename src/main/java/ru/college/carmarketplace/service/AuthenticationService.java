package ru.college.carmarketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.college.carmarketplace.model.requests.AuthenticationRequest;
import ru.college.carmarketplace.model.responses.AuthenticationResponse;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.model.responses.ResetResponse;

import java.io.IOException;

public interface AuthenticationService {
    ResetResponse reset(AuthenticationRequest request);

    ResetResponse sendResetToExist(AuthenticationRequest request);

    ResetResponse setPassword(AuthenticationRequest request);

    AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) throws IOException;

    void register(RegisterRequest registerRequest);

    boolean verify(RegisterRequest registerRequest);
}
