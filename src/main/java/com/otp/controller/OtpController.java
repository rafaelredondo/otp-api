package com.otp.controller;

import com.otp.model.ApiResponse;
import com.otp.model.OtpResponse;
import com.otp.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@Validated
@CrossOrigin("*")
@Tag(name = "OTP Controller", description = "API para gerenciamento de senhas de uso único (OTP)")
public class OtpController {
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @Operation(
        summary = "Geração de OTP",
        description = "Gera um novo OTP para o email fornecido e envia por email"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP gerado e enviado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email inválido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro ao gerar ou enviar OTP")
    })
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse> generateOtp(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email) {
        OtpResponse response = otpService.generateOtp(email);
        return ResponseEntity.ok(new ApiResponse(response.otp(), "OTP sent successfully"));
    }

    @Operation(
        summary = "Validação de OTP",
        description = "Valida um OTP fornecido para o email especificado"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP validado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "OTP inválido ou expirado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro interno durante validação")
    })
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateOtp(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email,
            @Parameter(description = "Código OTP a ser validado", required = true)
            @RequestParam 
            @NotBlank(message = "OTP cannot be empty")
            String otp) {
        boolean valid = otpService.validateOtp(email, otp);
        if (!valid) {
            throw new com.otp.exception.OtpValidationException("OTP inválido para o email informado");
        }
        return ResponseEntity.ok(new ApiResponse(null, "OTP validated successfully"));
    }

    @Operation(
        summary = "Revogação de OTP",
        description = "Revoga um OTP ativo para o email especificado"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP revogado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email inválido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erro ao revogar OTP")
    })
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse> revokeOtp(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email,
            @Parameter(description = "Motivo da revogação", required = true)
            @RequestParam String reason) {
        otpService.revokeOtp(email, reason);
        return ResponseEntity.ok(new ApiResponse(null, "OTP revoked successfully"));
    }
} 