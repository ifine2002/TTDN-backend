package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.validator.ValidImage;

@Getter
@Setter
public class ReqCategoryDTO {
  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  @ValidImage(
      message = "Invalid image file",
      maxSize = 1024 * 1024 * 10, // 10MB
      allowedExtensions = {"jpg", "jpeg", "png", "gif"}
  )
  private MultipartFile image;

  private boolean deleteImage;
}
