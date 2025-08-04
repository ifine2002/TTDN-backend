package vn.ifine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import vn.ifine.dto.validator.EnumPattern;
import vn.ifine.util.GenderEnum;

@Getter
public class ReqUpdateUser implements Serializable {
  @NotBlank(message = "fullName must be not blank")
  private String fullName;

  private String image;

  @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
  private String phone;

  @EnumPattern(name = "gender", regexp = "(?i)MALE|FEMALE|OTHER")
  private GenderEnum gender;

  @NotNull(message = "dateOfBirth must be not null")
  private LocalDate userDOB;

  @NotNull(message = "address must be not null")
  private String address;
}
