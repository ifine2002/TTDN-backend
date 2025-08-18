package vn.ifine.dto.response;

import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    //Số lượng người theo dõi mình
    private Long follower;

    //Số lượng người mình theo dõi
    private Long following;

    private Date createdAt;

    private Date updatedAt;

    private String createBy;

    private String updatedBy;

}

