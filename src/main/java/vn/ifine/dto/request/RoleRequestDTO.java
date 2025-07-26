package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import vn.ifine.model.Permission;
import vn.ifine.model.User;

@Getter
public class RoleRequestDTO {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  private List<Permission> permissions;
}
