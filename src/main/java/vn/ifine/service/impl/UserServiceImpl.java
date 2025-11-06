package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ifine.dto.request.ReqChangeInfo;
import vn.ifine.dto.request.ReqChangePassword;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ResInfoUser;
import vn.ifine.dto.response.ResUserFollow;
import vn.ifine.dto.response.ResUserSearch;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.exception.CustomException;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Follow;
import vn.ifine.model.Role;
import vn.ifine.model.User;
import vn.ifine.repository.FollowRepository;
import vn.ifine.repository.UserRepository;
import vn.ifine.service.FileService;
import vn.ifine.service.RoleService;
import vn.ifine.service.UserService;
import vn.ifine.specification.UserSpecification;

@Slf4j(topic = "USER-SERVICE-IMPL")
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final FollowRepository followRepository;
  private final FileService fileService;

  @Override
  public User getById(long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id = " + id));
    return user;
  }

  // for client
  @Override
  public ResInfoUser getInfoUser(Long id) {
    User user = this.getById(id);
    List<Follow> followers = followRepository.findByFollowingIdWithFollower(id);
    List<ResUserFollow> listFollowers = followers.stream()
        .map(f -> new ResUserFollow(
            f.getFollower().getId(),
            f.getFollower().getFullName(),
            f.getFollower().getImage()
        ))
        .toList();

    List<Follow> followings = followRepository.findByFollowerIdWithFollowing(id);
    List<ResUserFollow> listFollowings = followings.stream()
        .map(f -> new ResUserFollow(
            f.getFollowing().getId(),
            f.getFollowing().getFullName(),
            f.getFollowing().getImage()
        ))
        .toList();

    return ResInfoUser.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .image(user.getImage())
        .follower(listFollowers)
        .following(listFollowings)
        .build();
  }

  @Override
  @Transactional
  public UserResponse createUser(ReqCreateUser request) {
    // check email
    if (this.isEmailExist(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Email = " + request.getEmail() + " already exist");
    }
    User user = User.builder()
        .fullName(request.getFullName())
        .email(request.getEmail())
        .image((request.getImage() != null) ? fileService.upload(request.getImage()) : null)
        .phone(request.getPhone())
        .gender(request.getGender())
        .userDOB(request.getUserDOB())
        .address(request.getAddress())
        .status(request.getStatus())
        .build();
    String hashPassword = this.passwordEncoder.encode(request.getPassword());
    user.setPassword(hashPassword);
    if (request.getRole() != null) {
      Role role = roleService.getById(request.getRole().getId());
      user.setRole(role);
    }
    user = userRepository.save(user);
    UserResponse resUser = this.convertToUserResponse(user);
    log.info("User has been created successfully, id={}", resUser.getId());
    return resUser;
  }

  //For admin
  @Override
  @Transactional
  public UserResponse update(long id, ReqUpdateUser reqUser) {
    User user = this.getById(id);

    user.setFullName(reqUser.getFullName());
    if(reqUser.getImage() != null){
      user.setImage(fileService.upload(reqUser.getImage()));
    }
    if(reqUser.isDeleteImage()){
      user.setImage(null);
    }
    user.setPhone(reqUser.getPhone());
    user.setGender(reqUser.getGender());
    user.setUserDOB(reqUser.getUserDOB());
    user.setAddress(reqUser.getAddress());
    user.setStatus(reqUser.getStatus());
    if (reqUser.getRole() != null) {
      Role role = roleService.getById(reqUser.getRole().getId());
      user.setRole(role);
    }

    user = userRepository.save(user);
    log.info("User has been updated successfully, id={}", user.getId());
    return this.convertToUserResponse(user);
  }

  @Override
  public UserResponse changeInfo(String email, ReqChangeInfo request) {
    User user = this.getUserByEmail(email);
    user.setFullName(request.getFullName());
    if(request.getImage() != null){
      user.setImage(fileService.upload(request.getImage()));
    }
    if(request.isDeleteImage()){
      user.setImage(null);
    }
    user.setPhone(request.getPhone());
    user.setGender(request.getGender());
    user.setUserDOB(request.getUserDOB());
    user.setAddress(request.getAddress());

    user = userRepository.save(user);
    log.info("User has been updated successfully, id={}", user.getId());
    return this.convertToUserResponse(user);
  }

  @Override
  public void changePassword(String email, ReqChangePassword request) {
    User user = this.getUserByEmail(email);

    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new CustomException("Old password is incorrect");
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new CustomException("New password and confirm password do not match");
    }

    String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
    user.setPassword(encodedNewPassword);

    userRepository.save(user);
  }

  @Override
  public void remove(long id) {
    User user = this.getById(id);
    this.userRepository.deleteById(id);
    log.info("User has been removed successfully, id={}", user.getId());
  }

  @Override
  public User getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Bad credentials"));
    log.info("Get user by email successfully, id={}", user.getId());
    return user;
  }

  @Override
  public boolean isEmailExist(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public UserResponse convertToUserResponse(User user) {
    Long countFollower = followRepository.countByFollowingId(user.getId());
    Long countFollowing = followRepository.countByFollowerId(user.getId());
    return UserResponse.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .image(user.getImage())
        .phone(user.getPhone())
        .gender(user.getGender())
        .userDOB(user.getUserDOB())
        .address(user.getAddress())
        .status(user.getStatus())
        .role(user.getRole())
        .follower(countFollower)
        .following(countFollowing)
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .createBy(user.getCreatedBy())
        .updatedBy(user.getUpdatedBy())
        .build();
  }

  @Override
  public void updateUserToken(String token, String email) {
    User currentUser = this.getUserByEmail(email);
    if (currentUser != null) {
      currentUser.setRefreshToken(token);
      this.userRepository.save(currentUser);
    }
    log.info("Updated token success, userId= {}", currentUser.getId());
  }

  @Override
  public User getUserByRefreshAndEmail(String token, String email) {
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
  }

  @Override
  public ResultPaginationDTO getAll(Specification<User> spec, Pageable pageable) {
    Page<User> pageUser = userRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageUser.getTotalPages());
    rs.setTotalElements(pageUser.getTotalElements());
    // convert data
    List<UserResponse> listUser = pageUser.getContent()
        .stream().map(this::convertToUserResponse)
        .toList();

    rs.setResult(listUser);

    return rs;
  }

  @Override
  public ResultPaginationDTO searchUser(Pageable pageable, String keyword) {
    Specification<User> spec = UserSpecification.search(keyword);

    Page<User> pageUser = userRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageUser.getTotalPages());
    rs.setTotalElements(pageUser.getTotalElements());
    // convert data
    List<ResUserSearch> listUser = pageUser.getContent()
        .stream().map(this::convertToResUserSearch)
        .toList();

    rs.setResult(listUser);
    return rs;
  }

  private ResUserSearch convertToResUserSearch(User user){
    return ResUserSearch.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .image(user.getImage())
        .address(user.getAddress())
        .build();
  }
}