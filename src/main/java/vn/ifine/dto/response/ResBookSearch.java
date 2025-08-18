package vn.ifine.dto.response;

import java.time.LocalDate;
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
public class ResBookSearch {

  private Long id;

  private String name;

  private String image;

  private LocalDate publishedDate;

  private int ratingCount;

  private double averageRating;

  private String author;
}
