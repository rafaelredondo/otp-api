package com.otp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(SimpleEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Implementação de simulação que apenas registra o envio no log
        logger.info("Enviando email para: {}", to);
        logger.info("Assunto: {}", subject);
        logger.info("Conteúdo: {}", body);
        logger.info("Email enviado com sucesso!");
    }
} 