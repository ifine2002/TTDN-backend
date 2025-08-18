package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChangePassword {
  @NotBlank(message = "oldPassword must be not blank")
  private String oldPassword;

  @NotBlank(message = "newPassword must be not blank")
  private String newPassword;

  @NotBlank(message = "confirmPassword must be not blank")
  private String confirmPassword;
}
