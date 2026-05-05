package ru.college.carmarketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.college.carmarketplace.model.requests.AuthenticationRequest;
import ru.college.carmarketplace.model.responses.AuthenticationResponse;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.service.impl.AuthenticationServiceImpl;
import ru.college.carmarketplace.service.impl.LogoutServiceImpl;
import ru.college.carmarketplace.model.responses.ResetResponse;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationServiceImpl service;
  private final LogoutServiceImpl logoutServiceImpl;

  @PostMapping("/verify/email")
  public ResponseEntity<String> verifyEmail(@RequestBody @Valid RegisterRequest registerRequest) {
      boolean isVerified = service.verify(registerRequest);
      if (isVerified) {
        return ResponseEntity.ok("Email успешно подтвержден.");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при проверке кода");
      }

  }

  @PostMapping("/registration")
  public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
      service.register(request);
      return ResponseEntity.ok("Пользователь зарегистрирован. Проверьте email для подтверждения.");

  }

  // в сервис
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request, HttpServletResponse httpresponse
  ) {
    AuthenticationResponse response = service.authenticate(request, httpresponse);

    return ResponseEntity.ok(AuthenticationResponse.builder().accessToken(response.getAccessToken())
                    .id(response.getId())
                    .phoneNumber(response.getPhoneNumber())
            .name(response.getName())
            .email(response.getEmail())
            .role(response.getRole()).build());
  }

  @PostMapping("/refresh")
  public AuthenticationResponse refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    return service.refreshToken(request, response);
  }

  // в сервис или утил
  @PostMapping("/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

    logoutServiceImpl.logout(request, response, authentication);
  }

  @PostMapping("/password/reset")
  public ResponseEntity<ResetResponse> reset(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.reset(request));
  }

  @PostMapping("/password/set")
  public ResponseEntity<ResetResponse> set(@RequestBody AuthenticationRequest request)
  {
    return ResponseEntity.ok(service.setPassword(request));
  }

  @PostMapping("/send/new")
  public ResponseEntity<ResetResponse> sendNewCode(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.sendResetToExist(request));
  }

  @GetMapping("is/expired")
  public String isExpired(@RequestHeader("Authorization") String header) {
    return logoutServiceImpl.isAccessExpired(header);
  }

}
