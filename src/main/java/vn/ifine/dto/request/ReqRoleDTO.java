package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.ifine.model.Permission;

@Getter
@Setter
public class ReqRoleDTO {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  private boolean isActive;

  private List<Permission> permissions;
}
