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
public class ResComment {

  private Long id;
  private Long userId;
  private Long bookId;
  private String comment;
  private boolean isRatingComment;
  private Date createdAt;
  private Date updatedAt;
  private String createdBy;
  private String updatedBy;
}
