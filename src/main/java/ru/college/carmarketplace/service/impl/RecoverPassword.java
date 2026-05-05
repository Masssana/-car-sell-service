package ru.college.carmarketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.exception.CustomException;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.service.AuthActionProcess;

@RequiredArgsConstructor
@Service
public class RecoverPassword implements AuthActionProcess {
    private final UserRepository repository;
    @Override
    public void process(RegisterRequest registerRequest) {
        AppUser user = repository.findByEmail(registerRequest.getEmail());

        if (!registerRequest.getConfirmCode().equals(user.getResetPasswordCode())) {
            throw new CustomException("Код не сходится");
        }

        user.setResetPasswordCode(null);
        repository.save(user);
    }
}
