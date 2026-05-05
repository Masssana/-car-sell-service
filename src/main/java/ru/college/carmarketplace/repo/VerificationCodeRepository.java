package ru.college.carmarketplace.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.college.carmarketplace.model.entities.VerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);

    void deleteByCreatedAtBefore(LocalDateTime createdAtBefore);
    
    void deleteByEmail(String email);
}
