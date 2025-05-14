package com.otp.endtoend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.otp.service.OtpNotificationService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "encryption.key=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="
})
public class OtpEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OtpNotificationService notificationService;

    @Test
    void shouldGenerateAndValidateOtpSuccessfully() throws Exception {
        String email = "exemplo@teste.com";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);

        // 1. Gerar OTP
        String responseBody = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
                
        // Extrair o OTP da resposta (assumindo formato JSON: {"otp":"123456","message":"..."})
        String otp = responseBody.split("\"otp\":\"")[1].split("\"")[0];

        // 2. Validar OTP
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP validated successfully"));
    }

    @Test
    void shouldInvalidateOldOtpWhenNewOtpIsGenerated() throws Exception {
        String email = "exemplo2@teste.com";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);

        // 1. Gerar primeiro OTP
        String responseBody1 = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
                
        // Extrair o primeiro OTP
        String otp1 = responseBody1.split("\"otp\":\"")[1].split("\"")[0];

        // 2. Gerar segundo OTP
        String responseBody2 = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
                
        // Extrair o segundo OTP
        String otp2 = responseBody2.split("\"otp\":\"")[1].split("\"")[0];

        // 3. Validar o primeiro OTP (deve falhar)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp1))
                .andExpect(status().isBadRequest());

        // 4. Validar o segundo OTP (deve funcionar)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP validated successfully"));
    }
    
    @Test
    void shouldFailValidationWhenOtpIsRevoked() throws Exception {
        String email = "exemplo3@teste.com";
        String reason = "Security concern";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);

        // 1. Gerar OTP
        String responseBody = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
                
        // Extrair o OTP
        String otp = responseBody.split("\"otp\":\"")[1].split("\"")[0];

        // 2. Revogar OTP
        mockMvc.perform(post("/api/otp/revoke")
                .param("email", email)
                .param("reason", reason))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP revoked successfully"));

        // 3. Tentar validar o OTP revogado (deve falhar)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
    
    @Test
    void shouldFailAfterMaxAttemptsWithInvalidOtp() throws Exception {
        String email = "exemplo4@teste.com";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);

        // 1. Gerar OTP
        mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists());
                
        String invalidOtp = "999999"; // Um OTP inválido
        
        // 2. Primeira tentativa inválida
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", invalidOtp))
                .andExpect(status().isBadRequest());
                
        // 3. Segunda tentativa inválida
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", invalidOtp))
                .andExpect(status().isBadRequest());
                
        // 4. Terceira tentativa inválida
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", invalidOtp))
                .andExpect(status().isBadRequest());
                
        // 5. Quarta tentativa (deve bloquear por excesso de tentativas)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", invalidOtp))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
} 