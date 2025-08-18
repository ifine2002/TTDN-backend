package vn.ifine.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
  private long maxSize;
  private List<String> allowedExtensions;

  @Override
  public void initialize(ValidImage constraintAnnotation) {
    this.maxSize = constraintAnnotation.maxSize();
    this.allowedExtensions = Arrays.asList(constraintAnnotation.allowedExtensions());
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    // Nếu file null và không bắt buộc, coi như valid
    if (file == null || file.isEmpty()) {
      return true;
    }

    context.disableDefaultConstraintViolation();

    // Kiểm tra kích thước file
    if (file.getSize() > maxSize) {
      context.buildConstraintViolationWithTemplate(
              "File size exceeds maximum allowed size of " + (maxSize / (1024 * 1024)) + "MB")
          .addConstraintViolation();
      return false;
    }

    // Kiểm tra định dạng file
    String originalFilename = file.getOriginalFilename();
    if (originalFilename != null) {
      String fileExtension = getFileExtension(originalFilename).toLowerCase();
      if (!allowedExtensions.contains(fileExtension)) {
        context.buildConstraintViolationWithTemplate(
                "Only " + String.join(", ", allowedExtensions) + " file extensions are allowed")
            .addConstraintViolation();
        return false;
      }
    }

    // Kiểm tra MIME type
    String contentType = file.getContentType();
    if (contentType != null && !contentType.startsWith("image/")) {
      context.buildConstraintViolationWithTemplate("Only image files are allowed")
          .addConstraintViolation();
      return false;
    }

    return true;
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
  }
}