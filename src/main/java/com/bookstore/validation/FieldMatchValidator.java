package com.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String pattern;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        pattern = constraintAnnotation.patternOf();
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
            return (firstFieldValue == null && secondFieldValue == null) ||
                    (firstFieldValue != null && firstFieldValue.equals(secondFieldValue) &&
                            Pattern.matches(pattern, firstFieldValue));
        } catch (Exception e) {
            return false;
        }
    }
}
