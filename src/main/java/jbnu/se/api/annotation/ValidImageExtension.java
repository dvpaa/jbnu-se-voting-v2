package jbnu.se.api.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jbnu.se.api.validation.ImageExtensionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ImageExtensionValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidImageExtension {
    String message() default "file must be image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
