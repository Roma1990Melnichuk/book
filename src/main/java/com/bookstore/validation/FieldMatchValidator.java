package com.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Pattern;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String pattern;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        pattern = PasswordValidator.PASSWORD_PATTERN;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Field firstField = value.getClass().getDeclaredField(firstFieldName);
            Field secondField = value.getClass().getDeclaredField(secondFieldName);
            firstField.setAccessible(true);
            secondField.setAccessible(true);
            String firstFieldValue = (String) firstField.get(value);
            String secondFieldValue = (String) secondField.get(value);

            if (!Objects.equals(firstFieldValue, secondFieldValue)) {
                context.buildConstraintViolationWithTemplate("Passwords do not match")
                        .addPropertyNode(secondFieldName)
                        .addConstraintViolation();
                return false;
            }

            if (!Pattern.matches(pattern, firstFieldValue)) {
                context.buildConstraintViolationWithTemplate(
                        "Password does not meet the required pattern")
                        .addPropertyNode(firstFieldName)
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
