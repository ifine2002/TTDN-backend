package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.model.Role;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Getter
public class UserRequestDTO implements Serializable {
  @NotBlank(message = "fullName must be not blank")
  private String fullName;

  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "invalid email format")
  private String email;

  @NotBlank(message = "password must be not blank")
  private String password;

  private String image;

  @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
  private String phone;

  @EnumPattern(name = "gender", regexp = "(?i)MALE|FEMALE|OTHER")
  private GenderEnum gender;

  @NotNull(message = "dateOfBirth must be not null")
  private LocalDate userDOB;

  @NotNull(message = "address must be not null")
  private String address;

  @EnumPattern(name = "status", regexp = "(?i)ACTIVE|INACTIVE|NONE|DELETED")
  private UserStatus status;

  @NotNull(message = "role must be not null")
  private Role role;
}
