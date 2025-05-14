package com.otp.endtoend;

import com.otp.service.OtpNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
        String response = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
        String otp = response.replaceAll("\\D", ""); // extrai apenas os dígitos do OTP

        // 2. Validar OTP
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP validated successfully"));
    }

    @Test
    void shouldInvalidateOldOtpWhenNewOtpIsGenerated() throws Exception {
        String email = "exemplo@teste.com";
        when(notificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);

        // 1. Gerar primeiro OTP
        String response1 = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
        String otp1 = response1.replaceAll("\\D", "");

        // 2. Gerar segundo OTP
        String response2 = mockMvc.perform(post("/api/otp/generate")
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").exists())
                .andReturn().getResponse().getContentAsString();
        String otp2 = response2.replaceAll("\\D", "");

        // 3. Validar o primeiro OTP (deve falhar)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp1))
                .andExpect(status().is4xxClientError());

        // 4. Validar o segundo OTP (deve funcionar)
        mockMvc.perform(post("/api/otp/validate")
                .param("email", email)
                .param("otp", otp2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP validated successfully"));
    }
} 