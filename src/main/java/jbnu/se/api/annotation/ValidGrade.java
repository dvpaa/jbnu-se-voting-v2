package jbnu.se.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jbnu.se.api.validation.GradeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = GradeValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidGrade {
    String message() default "Invalid Grade";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
