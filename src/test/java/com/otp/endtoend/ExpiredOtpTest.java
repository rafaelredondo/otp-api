package com.otp.endtoend;

import com.otp.model.OtpHistory;
import com.otp.model.OtpStatus;
import com.otp.repository.OtpHistoryRepository;
import com.otp.service.EncryptionService;
import com.otp.service.OtpNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "encryption.key=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="
})
public class ExpiredOtpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OtpHistoryRepository otpHistoryRepository;
    
    @Autowired
    private EncryptionService encryptionService;

    @MockBean
    private OtpNotificationService notificationService;

    @Test
    @Transactional
    void shouldFailValidationWhenOtpIsExpired() throws Exception {
        // Given
        String email = "expired@teste.com";
        String otp = "123456";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);
        
        // 1. Gerar OTP normalmente
        mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk());
                
        // 2. Modificar o timestamp para simular uma expiração (31 minutos atrás)
        OtpHistory history = otpHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE)
                .orElseThrow();
                
        // Usar reflection para modificar o createdAt, já que não temos um setter público
        Field createdAtField = OtpHistory.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(history, Instant.now().minusSeconds(31 * 60)); // 31 minutos atrás
        
        otpHistoryRepository.save(history);
                
        // 3. Tentar validar o OTP expirado
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
} 