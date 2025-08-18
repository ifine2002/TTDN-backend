package vn.ifine.dto.response;

import jakarta.persistence.Column;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCategory {

  private Integer id;

  private String name;

  private String description;

  private String image;

  private Boolean isActive;

  private String createdBy;

  private String updatedBy;

  private Date createdAt;

  private Date updatedAt;
}
