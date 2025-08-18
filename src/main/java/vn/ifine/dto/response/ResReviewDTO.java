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
public class ResReviewDTO {

  private String fullName;

  private String image;

  private Long ratingId;

  private Long commentId;

  private Long userId;

  private Long stars;

  private String comment;

  private Date createdAt;

  private Date updatedAt;

}
