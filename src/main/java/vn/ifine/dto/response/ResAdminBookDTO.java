package vn.ifine.dto.response;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
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
public class ResAdminBookDTO {
  private Long bookId;

  private String bookName;

  private String description;
  private LocalDate publishedDate;
  private String bookFormat;
  private String bookSaleLink;
  private String language;
  private String imageBook;
  private String author;
  private Set<ResCategoryInBook> categories;

  private Long userId;
  private String fullName;
  private String avatar;
  private Date createdAt;
  private Date updatedAt;
  private String createdBy;
  private String updatedBy;
}
