package ru.college.carmarketplace.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.repo.TokenRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtServiceImpl jwtServiceImpl;

    @InjectMocks
    private LogoutServiceImpl logoutService;

    @Test
    void isAccessExpired_withoutBearer_returnsMessage() {
        assertThat(logoutService.isAccessExpired(null)).isEqualTo("Токена нет");
        assertThat(logoutService.isAccessExpired("Basic x")).isEqualTo("Токена нет");
    }

    @Test
    void isAccessExpired_validToken_returnsOkString() {
        when(jwtServiceImpl.isTokenExpired("tok")).thenReturn(false);

        assertThat(logoutService.isAccessExpired("Bearer tok")).isEqualTo("Токен действителен");
    }

    @Test
    void isAccessExpired_expiredToken_throws() {
        when(jwtServiceImpl.isTokenExpired("tok")).thenReturn(true);

        assertThatThrownBy(() -> logoutService.isAccessExpired("Bearer tok"))
                .isInstanceOf(ResponseStatusException.class);
    }
}
