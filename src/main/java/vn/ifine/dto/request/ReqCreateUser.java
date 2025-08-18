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
import vn.ifine.util.UserStatus;

@Getter
@Setter
public class ReqCreateUser implements Serializable {
  @NotBlank(message = "fullName must be not blank")
  private String fullName;

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "invalid email format")
  private String email;

  @NotBlank(message = "password must be not blank")
  private String password;

  @ValidImage(
      message = "Invalid image file",
      maxSize = 1024 * 1024 * 10, // 10MB
      allowedExtensions = {"jpg", "jpeg", "png"}
  )
  private MultipartFile image;

  @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
  private String phone;

  @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
  private GenderEnum gender;

  private LocalDate userDOB;

  private String address;

  @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE|DELETED")
  private UserStatus status;

  @NotNull(message = "role must be not null")
  private Integer roleId;
}
