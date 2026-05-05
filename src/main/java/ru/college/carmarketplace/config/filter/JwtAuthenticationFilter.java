package ru.college.carmarketplace.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.college.carmarketplace.service.impl.JwtServiceImpl;
import ru.college.carmarketplace.repo.TokenRepository;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtServiceImpl jwtServiceImpl;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    checkHeader(request, response, filterChain, authHeader);

    jwt = authHeader.substring(7);
    userEmail = jwtServiceImpl.extractUsername(jwt);

    checkIsAuthenticated(request, userEmail, jwt);

    filterChain.doFilter(request, response);
  }

  private void checkHeader(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String authHeader) throws ServletException, IOException {
    if(authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }
  }

  private void checkIsAuthenticated(HttpServletRequest request, String userEmail, String jwt){
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      var isTokenValid = tokenRepository.findByToken(jwt)
              .map(t -> !t.isExpired() && !t.isRevoked())
              .orElse(false);
      isTokenValid(jwt, userDetails, isTokenValid, request);
    }

  }

  private void isTokenValid(String jwt, UserDetails userDetails, boolean isTokenValid, HttpServletRequest request){
    if (jwtServiceImpl.isTokenValid(jwt, userDetails) && isTokenValid) {
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
      );
      authToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
      );
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
  }

}
