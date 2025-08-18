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
public class ResRatingDTO {
  private Long id;

  private long stars;

  private Long userId;

  private Long bookId;

  private Date createdAt;

  private Date updatedAt;

  private String createdBy;

  private String updatedBy;
}
