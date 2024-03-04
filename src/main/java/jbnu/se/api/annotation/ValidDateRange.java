package jbnu.se.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jbnu.se.api.validation.DateRangeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidDateRange {
    String message() default "startDate must be before endDate";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
