package vn.ifine.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResFeedBack {
  private double averageRating;
  private int ratingCount;

  private int totalOneStar;
  private int totalTwoStar;
  private int totalThreeStar;
  private int totalFourStar;
  private int totalFiveStar;

}
