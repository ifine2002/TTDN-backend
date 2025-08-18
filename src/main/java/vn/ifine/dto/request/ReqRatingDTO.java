package vn.ifine.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqRatingDTO {
  @NotNull(message = "userId must be not null")
  private Long userId;

  @NotNull(message = "bookId must be not null")
  private Long bookId;

  @NotNull(message = "stars must be not null")
  @Min(value = 1, message = "stars must be at least 1")
  @Max(value = 5, message = "stars must be at most 5")
  private Long stars;
}
