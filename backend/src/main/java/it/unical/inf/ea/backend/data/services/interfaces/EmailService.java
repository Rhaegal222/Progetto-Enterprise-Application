package it.unical.inf.ea.backend.data.services.interfaces;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String to, String subject, String text) throws MessagingException;
}
