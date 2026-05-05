package ru.college.carmarketplace.service;

public interface EmailService {
    void sendEmailConfirmation(String emailToSend, String confirmationCode);
}
