package jbnu.se.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jbnu.se.api.validation.CsvExtensionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CsvExtensionValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidCsvExtension {
    String message() default "file must be .csv";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
