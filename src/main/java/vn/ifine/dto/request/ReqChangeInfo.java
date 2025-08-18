package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.dto.validator.ValidImage;
import vn.ifine.util.GenderEnum;

@Getter
@Setter
public class ReqChangeInfo implements Serializable {
  @NotBlank(message = "fullName must be not blank")
  private String fullName;

  @ValidImage(
      message = "Invalid image file",
      maxSize = 1024 * 1024 * 10, // 10MB
      allowedExtensions = {"jpg", "jpeg", "png", "gif"}
  )
  private MultipartFile image;

  private boolean deleteImage;

  @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
  private String phone;

  @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
  private GenderEnum gender;

  @NotNull(message = "dateOfBirth must be not null")
  private LocalDate userDOB;

  @NotNull(message = "address must be not null")
  private String address;
}
