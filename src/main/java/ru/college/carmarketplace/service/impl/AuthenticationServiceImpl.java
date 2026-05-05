package ru.college.carmarketplace.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ru.college.carmarketplace.model.responses.AuthenticationResponse;
import ru.college.carmarketplace.model.entities.VerificationCode;
import ru.college.carmarketplace.repo.VerificationCodeRepository;
import ru.college.carmarketplace.model.requests.AuthenticationRequest;
import ru.college.carmarketplace.model.responses.ErrorResponse;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.exception.ValidationException;
import ru.college.carmarketplace.service.AuthActionProcess;
import ru.college.carmarketplace.service.AuthenticationService;
import ru.college.carmarketplace.model.entities.Token;
import ru.college.carmarketplace.repo.TokenRepository;
import ru.college.carmarketplace.enums.TokenType;
import ru.college.carmarketplace.enums.Role;
import ru.college.carmarketplace.model.responses.ResetResponse;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.utils.EmailCodeGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtServiceImpl jwtServiceImpl;
  private final AuthenticationManager authenticationManager;
  private final VerificationCodeRepository verificationCodeRepository;
  private final Map<String, AuthActionProcess> authAction = new HashMap<>();
  private static final Integer MIN_PASS_LENGTH = 8;
  public static final Integer THIRTY_DAYS_IN_SECONDS = 5184000;

  @PostConstruct
  public void init() {
    authAction.put("confirmEmail", new ConfirmEmail(verificationCodeRepository, repository));
    authAction.put("recoveryPassword", new RecoverPassword(repository));
  }

  public boolean verify(RegisterRequest registerRequest) {
    authAction.get(registerRequest.getType()).process(registerRequest);
    return true;
  }

  public void register(RegisterRequest registerRequest) {
    ErrorResponse errorResponse = new ErrorResponse();
    
    validateFieldsOnRegister(errorResponse, registerRequest);

    validateExistingEmail(registerRequest, errorResponse);

    validatePasswordLength(errorResponse, registerRequest);
    
    throwErrorResponse(errorResponse);

    repository.save(buildUser(registerRequest));

    String confirmCode = EmailCodeGenerator.generateRandomConfirm();
    EmailCodeGenerator.sendEmailConfirmation(registerRequest.getEmail(), confirmCode);

    saveVerificationCodeToDatabase(registerRequest, confirmCode);
  }
  
  private AppUser buildUser(RegisterRequest registerRequest) {
    var user = AppUser.builder()
            .name(registerRequest.getName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .confirmed(false)
            .build();
    user.setRole(Role.USER);
    return user;
  }
  
  private void throwErrorResponse(ErrorResponse errorResponse) {
    if(!errorResponse.getErrors().isEmpty()){
      throw new ValidationException(errorResponse.getErrors());
    }
  }
  
  private void saveVerificationCodeToDatabase(RegisterRequest registerRequest, String confirmCode) {
    VerificationCode verificationCode = new VerificationCode(registerRequest.getEmail(), confirmCode);
    verificationCodeRepository.save(verificationCode);
  }
  
  private void validateExistingEmail(RegisterRequest registerRequest, ErrorResponse errorResponse){
    if (repository.existsByEmail(registerRequest.getEmail())) {
      errorResponse.addError("email", "такая почта уже существует");
    }
  }
  
  private void validatePasswordLength(ErrorResponse errorResponse, RegisterRequest registerRequest){
    if(registerRequest.getPassword().length() < MIN_PASS_LENGTH){
      errorResponse.addError("password", "Пароль должен быть не меньше 8 символов");
    }
  }
  
  private void validateFieldsOnRegister(ErrorResponse errorResponse, RegisterRequest registerRequest){
    if(registerRequest.getName().isEmpty() && registerRequest.getEmail().isEmpty() && registerRequest.getPassword().isEmpty()){
      errorResponse.addError("name", "Поле имя пустое");
      errorResponse.addError("email", "Поле почты пустое");
      errorResponse.addError("password", "Поле пароля пустое");
    }
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
    ErrorResponse errorResponse = new ErrorResponse();

    validateEmailField(request, errorResponse);

    validatePasswordField(request, errorResponse);

    throwResponseErrors(errorResponse);

    var user = repository.findByEmail(request.getEmail());

    validateFieldsEmailAndPasswordAndThrowException(user, errorResponse);

    validatePasswordAndThrowErrorsWithException(request, user, errorResponse);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    var name = user.getName();
    var email = user.getEmail();
    var role = user.getRole();
    var accessToken = jwtServiceImpl.generateToken(user);
    var refreshToken = jwtServiceImpl.generateRefreshToken(user);
    
    addResponseCookie(response, refreshToken);
    return getAuthenticationResponse(user, refreshToken, accessToken, name, email, role);
  }

  private void addResponseCookie(HttpServletResponse response, String refreshToken) {
    ResponseCookie refreshCookie = ResponseCookie
            .from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(THIRTY_DAYS_IN_SECONDS).build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
  }

  private void validatePasswordAndThrowErrorsWithException(AuthenticationRequest request, AppUser user, ErrorResponse errorResponse) {
    if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
      errorResponse.addError("email", "Несуществующая почта или неверный пароль");
      errorResponse.addError("password", "Несуществующая почта или неверный пароль");
      throw new ValidationException(errorResponse.getErrors());
    }
  }

  private void validateFieldsEmailAndPasswordAndThrowException(AppUser user, ErrorResponse errorResponse) {
    if(user == null){
      errorResponse.addError("email", "Несуществующая почта или неверный пароль");
      errorResponse.addError("password", "Несуществующая почта или неверный пароль");
      throw new ValidationException(errorResponse.getErrors());
    }
  }

  private void throwResponseErrors(ErrorResponse errorResponse) {
    if(!errorResponse.getErrors().isEmpty()) {
      throw new ValidationException(errorResponse.getErrors());
    }
  }

  private void validatePasswordField(AuthenticationRequest request, ErrorResponse errorResponse) {
    if(request.getPassword() == null || request.getPassword().isEmpty()){
      errorResponse.addError("password", "Поле пароля не может быть пустым");
    }
  }

  private void validateEmailField(AuthenticationRequest request, ErrorResponse errorResponse) {
    if(request.getEmail() == null || request.getEmail().isEmpty()){
      errorResponse.addError("email", "Поле почты не может быть пустым");
    }
  }

  private AuthenticationResponse getAuthenticationResponse(AppUser user, String refreshToken, String accessToken, String name, String email, Role role) {
    revokeAllUserTokens(user);
    saveRefreshToken(user, refreshToken);
    return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .id(user.getId())
            .refreshToken(refreshToken)
            .phoneNumber(user.getPhoneNumber())
            .name(name)
            .email(email)
            .role(role)
            .build();
  }

  private void saveRefreshToken(AppUser user, String refreshToken) {
    var token = Token.builder()
            .user(user)
            .token(refreshToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(AppUser user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
      final String refreshToken = extractRefreshTokenFromCookie(request);
      if (refreshToken == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token is missing");
        return null;
      }
      final String userEmail = jwtServiceImpl.extractUsername(refreshToken);
      if (userEmail != null) {
        var user = repository.findByEmail(userEmail);
        if (jwtServiceImpl.isTokenValid(refreshToken, user)) {
          var accessToken = jwtServiceImpl.generateToken(user);

          var authResponse = AuthenticationResponse.builder()
                  .accessToken(accessToken)
                  .id(user.getId())
                  .name(user.getName())
                  .email(user.getEmail())
                  .role(user.getRole())
                  .build();

          return authResponse;

        } else {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
        }
      } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid refresh token");
      }
      return AuthenticationResponse.builder().build();
    }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null){
      return null;
    }
    for (Cookie cookie : request.getCookies()) {
      if ("refreshToken".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }

  public ResetResponse reset(AuthenticationRequest request) {
    ErrorResponse errorResponse = new ErrorResponse();
    AppUser user = repository.findByEmail(request.getEmail());
    validateEmail(user, errorResponse);

    verificationCodeRepository.deleteByEmail(request.getEmail());
    String confirmCode = EmailCodeGenerator.generateRandomConfirm();
    VerificationCode verificationCode = new VerificationCode(confirmCode, user.getEmail());
    verificationCodeRepository.save(verificationCode);
    EmailCodeGenerator.sendEmailConfirmation(request.getEmail(), confirmCode);
    return new ResetResponse("Код отправлен на вашу почту");
  }

  public ResetResponse sendResetToExist(AuthenticationRequest request) {
    AppUser user = repository.findByEmail(request.getEmail());
    verificationCodeRepository.deleteByEmail(request.getEmail());
    String confirmCode = EmailCodeGenerator.generateRandomConfirm();
    VerificationCode verificationCode = new VerificationCode(confirmCode, user.getEmail());
    verificationCodeRepository.save(verificationCode);
    EmailCodeGenerator.sendEmailConfirmation(request.getEmail(), confirmCode);
    return new ResetResponse("Новый код отправлен вам на почту");
  }

  public ResetResponse setPassword(AuthenticationRequest request) {
    ErrorResponse errorResponse = new ErrorResponse();
    AppUser user = repository.findByEmail(request.getEmail());
    
    validateEmail(user, errorResponse);

    validatePasswordInAuthentication(request, errorResponse);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setResetPasswordCode(null);
    repository.save(user);
    return new ResetResponse("Пароль успешно изменен");
  }

  private void validatePasswordInAuthentication(AuthenticationRequest request, ErrorResponse errorResponse) {
    if (request.getPassword().length() < MIN_PASS_LENGTH) {
      errorResponse.addError("password", "Пароль не может быть меньше восьми символов");
    }
  }

  private void validateEmail(AppUser user, ErrorResponse errorResponse) {
    if (user == null) {
      errorResponse.addError("email", "Такая почта не найдена");
      throw new ValidationException(errorResponse.getErrors());
    }
  }
}
