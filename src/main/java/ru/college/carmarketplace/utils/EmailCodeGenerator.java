package ru.college.carmarketplace.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public final class EmailCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] DIGITS = "0123456789".toCharArray();

    private EmailCodeGenerator() {
    }

    public static String generateRandomConfirm() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(DIGITS[RANDOM.nextInt(DIGITS.length)]);
        }
        return sb.toString();
    }

    public static void sendEmailConfirmation(String email, String code) {
        log.debug("Confirmation code for {}: {}", email, code);
    }
}
