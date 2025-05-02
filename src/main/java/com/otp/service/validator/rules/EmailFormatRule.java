package com.otp.service.validator.rules;

import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;

@Component
public class EmailFormatRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(EmailFormatRule.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Override
    public boolean validate(ValidationContext context) {
        if (context.email() == null || !EMAIL_PATTERN.matcher(context.email()).matches()) {
            logger.warn("Invalid email format: {}", context.email());
            return false;
        }
        return true;
    }
} 