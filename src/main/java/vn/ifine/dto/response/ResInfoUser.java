package vn.ifine.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResInfoUser {
  private Long id;

  private String fullName;

  private String email;

  private String image;

  private List<ResUserFollow> follower;

  private List<ResUserFollow> following;
}
