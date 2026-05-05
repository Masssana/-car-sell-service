package ru.college.carmarketplace.controller.support;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.college.carmarketplace.repo.TokenRepository;
import ru.college.carmarketplace.service.impl.JwtServiceImpl;

/**
 * Mocks dependencies required by {@link ru.college.carmarketplace.config.filter.JwtAuthenticationFilter}
 * when {@code @WebMvcTest} loads the application context.
 */
public abstract class ControllerTestSupport {

    @MockitoBean
    protected JwtServiceImpl jwtServiceImpl;

    @MockitoBean
    protected UserDetailsService userDetailsService;

    @MockitoBean
    protected TokenRepository tokenRepository;
}
