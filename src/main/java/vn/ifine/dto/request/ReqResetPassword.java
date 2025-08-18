package vn.ifine.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqResetPassword {
  private String newPassword;

  private String confirmPassword;
}
