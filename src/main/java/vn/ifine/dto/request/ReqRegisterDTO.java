package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ReqRegisterDTO {
  @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "invalid email format")
  private String email;

  @NotBlank(message = "password must be not blank")
  private String password;

  @NotBlank(message = "fullName must be not blank")
  private String fullName;
}
