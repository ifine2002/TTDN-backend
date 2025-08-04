package vn.ifine.dto.response;

import jakarta.persistence.Column;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.ifine.model.Role;
import vn.ifine.util.GenderEnum;
import vn.ifine.util.UserStatus;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse{

    private Long id;

    private String fullName;

    private String email;

    private String image;

    private String phone;

    private GenderEnum gender;

    private LocalDate userDOB;

    private String address;

    private UserStatus status;

    private Role role;

    private Date createdAt;

    private Date updatedAt;

//    @CreatedBy
//    @Column(name = "create_by")
//    T createBy;
//
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    T updatedBy;

}

