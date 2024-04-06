package jbnu.se.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jbnu.se.api.annotation.ValidCandidateType;
import jbnu.se.api.domain.CandidateType;

import java.util.Arrays;
import java.util.List;

public class CandidateTypeValidator implements ConstraintValidator<ValidCandidateType, String> {

    private final List<String> allowedValues = Arrays.stream(CandidateType.values())
            .map(Enum::name)
            .toList();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return allowedValues.contains(value);
    }
}
