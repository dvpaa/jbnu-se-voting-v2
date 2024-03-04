package jbnu.se.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jbnu.se.api.annotation.ValidDateRange;
import jbnu.se.api.request.ElectionRequest;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value instanceof ElectionRequest.Period period) {
            return period.getStartDate().isBefore(period.getEndDate());
        }
        return true;
    }
}
