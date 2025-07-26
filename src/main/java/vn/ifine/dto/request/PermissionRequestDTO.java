package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import vn.ifine.model.Role;

@Getter
public class PermissionRequestDTO {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "apiPath must be not blank")
  private String apiPath;

  @NotBlank(message = "method must be not blank")
  private String method;

  @NotBlank(message = "module must be not blank")
  private String module;
}
