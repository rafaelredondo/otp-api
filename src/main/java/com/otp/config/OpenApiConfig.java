package com.otp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI otpServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desenvolvimento");

        Contact contact = new Contact();
        contact.setEmail("suporte@otpservice.com");
        contact.setName("Suporte OTP Service");
        contact.setUrl("https://www.otpservice.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("OTP Service API")
                .version("1.0")
                .contact(contact)
                .description("API para geração e validação de senhas de uso único (OTP)")
                .termsOfService("https://www.otpservice.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
} 