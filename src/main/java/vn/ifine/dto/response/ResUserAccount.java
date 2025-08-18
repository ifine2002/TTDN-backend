package vn.ifine.dto.response;

import lombok.Getter;
import lombok.Setter;
import vn.ifine.model.Role;

@Setter
@Getter
public class ResUserAccount {
  private long id;
  private String email;
  private String fullName;
  private String image;
  private Role role;
}
