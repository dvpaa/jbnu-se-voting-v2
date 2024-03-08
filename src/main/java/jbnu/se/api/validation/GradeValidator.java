package jbnu.se.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jbnu.se.api.annotation.ValidGrade;
import jbnu.se.api.domain.Grade;

import java.util.Arrays;
import java.util.List;

public class GradeValidator implements ConstraintValidator<ValidGrade, String> {

    private final List<String> allowedValues = Arrays.stream(Grade.values())
            .map(Enum::name)
            .toList();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return allowedValues.contains(value);
    }
}
