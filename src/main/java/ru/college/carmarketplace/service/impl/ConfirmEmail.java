package ru.college.carmarketplace.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.college.carmarketplace.exception.CustomException;
import ru.college.carmarketplace.model.requests.RegisterRequest;
import ru.college.carmarketplace.model.entities.AppUser;
import ru.college.carmarketplace.model.entities.VerificationCode;
import ru.college.carmarketplace.repo.UserRepository;
import ru.college.carmarketplace.repo.VerificationCodeRepository;
import ru.college.carmarketplace.service.AuthActionProcess;

@RequiredArgsConstructor
@Service
public class ConfirmEmail implements AuthActionProcess {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository repository;

    @Override
    public void process(RegisterRequest registerRequest) throws CustomException {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(registerRequest.getEmail())
                .orElseThrow(() -> new CustomException("Код подтверждения не найден"));

        validateVerificationCode(registerRequest, verificationCode);

        var user = repository.findByEmail(registerRequest.getEmail());
        extracted(user);

        user.setConfirmed(true);
        repository.save(user);

        verificationCodeRepository.delete(verificationCode);
    }

    private void extracted(AppUser user) {
        if (user == null) {
            throw new CustomException("Пользователь не найден");
        }
    }

    private void validateVerificationCode(RegisterRequest registerRequest, VerificationCode verificationCode) {
        if (!verificationCode.getConfirmCode().equals(registerRequest.getConfirmCode())) {
            throw new CustomException("Код не сходится");
        }
    }
}
