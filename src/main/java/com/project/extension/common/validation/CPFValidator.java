package com.project.extension.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // deixa @NotNull/@NotBlank cuidar disso
        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 11) return false;
        if (digits.chars().distinct().count() == 1) return false; // ex: 111.111.111-11

        int sum1 = 0;
        for (int i = 0; i < 9; i++) sum1 += (digits.charAt(i) - '0') * (10 - i);
        int d1 = (sum1 * 10 % 11) % 10;
        if (d1 != digits.charAt(9) - '0') return false;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) sum2 += (digits.charAt(i) - '0') * (11 - i);
        int d2 = (sum2 * 10 % 11) % 10;
        return d2 == digits.charAt(10) - '0';
    }
}
