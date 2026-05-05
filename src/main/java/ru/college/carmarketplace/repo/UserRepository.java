package ru.college.carmarketplace.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.college.carmarketplace.model.entities.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {

  AppUser findByEmail(String email);
  boolean existsByEmail(String email);
  
  List<AppUser> findByConfirmedFalseAndCreatedAtBefore(LocalDateTime createdAtBefore);
}
