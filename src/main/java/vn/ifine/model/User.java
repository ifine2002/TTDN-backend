package vn.ifine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractUserEntity<Long>{
  @Column(name = "full_name")
  private String fullName;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "image")
  private String image;

  @Column(name = "phone")
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private GenderEnum gender;

  @Column(name = "user_DOB")
  private LocalDate userDOB;

  @Column(name = "address")
  private String address;

  @Column(name = "refresh_token", columnDefinition = "MEDIUMTEXT")
  private String refreshToken;

  @Enumerated(EnumType.STRING)
//  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status")
  private UserStatus status;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;
}