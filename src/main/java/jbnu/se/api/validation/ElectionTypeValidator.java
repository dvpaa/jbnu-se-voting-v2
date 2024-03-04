package jbnu.se.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jbnu.se.api.annotation.ValidElectionType;
import jbnu.se.api.domain.ElectionType;

import java.util.Arrays;
import java.util.List;

public class ElectionTypeValidator implements ConstraintValidator<ValidElectionType, String> {

    private final List<String> allowedValues = Arrays.stream(ElectionType.values())
            .map((Enum::name))
            .toList();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return allowedValues.contains(value);
    }
}
