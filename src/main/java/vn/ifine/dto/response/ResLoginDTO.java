package vn.ifine.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ifine.model.Role;

@Getter
@Setter
public class ResLoginDTO {
  @JsonProperty("access_token")
  private String accessToken;
  private UserLogin user;

  @Setter
  @Getter
  @AllArgsConstructor
  public static class UserLogin {
    private long id;
    private String email;
    private String fullName;
    private Role role;
  }

  @Setter
  @Getter
  public static class UserInsideToken {
    private long id;
    private String email;
    private String fullName;
  }
}
