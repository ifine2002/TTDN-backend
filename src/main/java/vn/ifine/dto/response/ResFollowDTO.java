package vn.ifine.dto.response;

import java.util.Date;
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
public class ResFollowDTO {
  private Long id;

  private Long followerId;

  private Long followingId;

  private Date createdAt;

  private String createdBy;
}
