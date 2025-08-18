package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.dto.validator.ValidImage;
import vn.ifine.model.Category;
import vn.ifine.util.BookStatus;

@Getter
@Setter
public class ReqBookDTO {

  @NotBlank(message = "name must be not blank")
  private String name;

  @NotBlank(message = "description must be not blank")
  private String description;

  @ValidImage(
      message = "Invalid image file",
      maxSize = 1024 * 1024 * 10, // 10MB
      allowedExtensions = {"jpg", "jpeg", "png", "gif"}
  )
  private MultipartFile image;

  private boolean deleteImage;

  @NotNull(message = "publishedDate must be not null")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate publishedDate;

  @NotBlank(message = "bookFormat must be not blank")
  private String bookFormat;

  @NotBlank(message = "bookSaleLink must be not blank")
  private String bookSaleLink;

  @NotBlank(message = "language must be not blank")
  private String language;

  @NotBlank(message = "author must be not blank")
  private String author;

  @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE|DELETED")
  private BookStatus status;

  private Set<Integer> categoryIds;

}
