package ru.college.carmarketplace.cleanup;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.college.carmarketplace.repo.VerificationCodeRepository;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VerificationCodeCleanup {
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 120000, initialDelay = 5000)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cleanUp() {
        LocalDateTime today = LocalDateTime.now();
        verificationCodeRepository.deleteByCreatedAtBefore(today.minusMinutes(3));
    }

    @Scheduled(fixedRate = 300000, initialDelay = 10000)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cleanExpiredUsers(){
        LocalDateTime today = LocalDateTime.now().minusMinutes(10);
        List<AppUser> expiredUsers = userRepository.findByConfirmedFalseAndCreatedAtBefore(today);
        userRepository.deleteAll(expiredUsers);
    }
}
