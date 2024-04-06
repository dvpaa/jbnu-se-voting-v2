package jbnu.se.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jbnu.se.api.validation.CandidateTypeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CandidateTypeValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidCandidateType {
    String message() default "Invalid candidate type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
