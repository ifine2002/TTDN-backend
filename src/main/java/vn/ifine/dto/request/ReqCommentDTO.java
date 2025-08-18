package vn.ifine.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReqCommentDTO {
  private String comment;
  private boolean isRatingComment;
  private Long userId;
  private Long bookId;
}
