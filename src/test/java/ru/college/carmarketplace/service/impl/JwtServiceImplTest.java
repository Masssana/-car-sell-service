package ru.college.carmarketplace.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.enums.Role;
import ru.college.carmarketplace.model.entities.AppUser;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private static final String SECRET_BASE64 = Base64.getEncoder().encodeToString(
            "12345678901234567890123456789012".getBytes()
    );

    private final JwtServiceImpl jwtService = new JwtServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_BASE64);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 5_184_000_000L);
    }

    @Test
    void generateToken_andExtractUsername_roundTrip() {
        AppUser user = AppUser.builder()
                .id(42)
                .name("Иван")
                .email("ivan@example.com")
                .password("secret")
                .role(Role.USER)
                .confirmed(true)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("ivan@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenExpired_invalidToken_throwsUnauthorized() {
        assertThatThrownBy(() -> jwtService.isTokenExpired("not-a-jwt"))
                .isInstanceOf(ResponseStatusException.class);
    }
}
