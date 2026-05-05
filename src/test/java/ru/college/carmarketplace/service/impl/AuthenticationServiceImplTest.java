package ru.college.carmarketplace.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.college.carmarketplace.exception.ValidationException;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.repo.TokenRepository;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.repo.VerificationCodeRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository repository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtServiceImpl jwtServiceImpl;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void initAuthActions() {
        ReflectionTestUtils.invokeMethod(authenticationService, "init");
    }

    @Test
    void register_rejectsWhenAllMainFieldsEmpty() {
        RegisterRequest req = RegisterRequest.builder()
                .name("")
                .email("")
                .password("")
                .build();

        assertThatThrownBy(() -> authenticationService.register(req))
                .isInstanceOf(ValidationException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void register_rejectsWhenEmailAlreadyExists() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Иван")
                .email("a@b.ru")
                .password("password123")
                .build();
        when(repository.existsByEmail("a@b.ru")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(req))
                .isInstanceOf(ValidationException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void register_rejectsShortPassword() {
        RegisterRequest req = RegisterRequest.builder()
                .name("Иван")
                .email("a@b.ru")
                .password("short")
                .build();
        when(repository.existsByEmail("a@b.ru")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.register(req))
                .isInstanceOf(ValidationException.class);

        verify(repository, never()).save(any());
    }
}
