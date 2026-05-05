package ru.college.carmarketplace.annotations;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import ru.college.carmarketplace.enums.Role;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.service.JwtService;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class IsAdminAspect {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Before("@annotation(ru.college.carmarketplace.annotations.AdminOnly)")
    public void isAdmin() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        Integer userId = getCurrentUserId(request);
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(user.getRole() != Role.ADMIN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный заголовок авторизации");
        }

        String token = authHeader.substring(7);
        Integer userId = jwtService.extractUserId(token);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный токен: ID пользователя не найден");
        }
        return userId;
    }

}
