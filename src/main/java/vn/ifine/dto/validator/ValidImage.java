package vn.ifine.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageValidator.class)
@Documented
public @interface ValidImage {
  String message() default "Invalid image file";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  long maxSize() default 1024 * 1024 * 5; // 5MB mặc định
  String[] allowedExtensions() default {"jpg", "jpeg", "png", "gif"};
}