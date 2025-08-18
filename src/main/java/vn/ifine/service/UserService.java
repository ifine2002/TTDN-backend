package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.request.ReqChangePassword;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqChangeInfo;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ResInfoUser;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.model.User;
import vn.ifine.util.UserStatus;

@Service
public interface UserService {

  User getById(long id);

  UserResponse createUser(ReqCreateUser request);

  UserResponse update(long id, ReqUpdateUser reqUser);

  void remove(long id);

  User getUserByEmail(String email);

  boolean isEmailExist(String email);

  UserResponse convertToUserResponse(User user);

  void updateUserToken(String token, String email);

  User getUserByRefreshAndEmail(String token, String email);

  ResultPaginationDTO getAll(Specification<User> spec, Pageable pageable);


  UserResponse changeInfo(String email, ReqChangeInfo request);

  void changePassword(String email, ReqChangePassword request);

  ResultPaginationDTO searchUser(Pageable pageable, String keyword);

  ResInfoUser getInfoUser(Long id);
}
