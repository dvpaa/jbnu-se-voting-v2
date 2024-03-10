package jbnu.se.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jbnu.se.api.annotation.ValidImageExtension;
import org.springframework.web.multipart.MultipartFile;

public class ImageExtensionValidator implements ConstraintValidator<ValidImageExtension, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        if (value instanceof MultipartFile file) {
            String contentType = file.getContentType();
            if (contentType == null) {
                return false;
            }
            return contentType.startsWith("image");
        }
        return false;
    }
}
