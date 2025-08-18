package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqChangePassword;
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqChangeInfo;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.model.User;
import vn.ifine.service.UserService;
import vn.ifine.util.UserStatus;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> create(
      @Valid ReqCreateUser reqUser) {
    log.info("Request add user, {} {}", reqUser.getEmail(), reqUser.getFullName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a user success",
            this.userService.createUser(reqUser)));
  }

  @GetMapping("/profile/{id}")
  public ResponseEntity<ApiResponse<?>> getProfileUser(@PathVariable @Min(1) Long id) {
    log.info("Request get profile user, id={}", id);
    // check exist by id
    return ResponseEntity.ok()
        .body(ApiResponse.success("Get user detail success",
            userService.getInfoUser(id)));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable @Min(1) long id,
      @Valid ReqUpdateUser request) {
    log.info("Request update user, id={}", id);
    UserResponse user = userService.update(id, request);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a user success",
            user));
  }

  @PutMapping(value = "/change-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> changeInfoForUser(Principal principal,
      @Valid ReqChangeInfo request) {
    log.info("Request change info user, emailUser={}", principal.getName());
    UserResponse user = userService.changeInfo(principal.getName(), request);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Change info user success",
            user));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") @Min(1) long id) {
    log.info("Request remove user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a user success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getAllUser(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all user success",
            this.userService.getAll(spec, pageable)));
  }

  // Change password
  @PatchMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(Principal principal,
      @RequestBody @Valid ReqChangePassword request) {
    log.info("Request change user password, {} {}", request.getOldPassword(), request.getNewPassword());
    userService.changePassword(principal.getName(), request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Change user password success",
            null));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<?>> searchPage(@RequestParam String keyword, Pageable pageable) {
    log.info("Request search user in search page, keyword={}", keyword);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Search user success",
            userService.searchUser(pageable, keyword)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable @Min(1) long id) {
    log.info("Request get user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);
    UserResponse resUser = userService.convertToUserResponse(user);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a user success",
            resUser));
  }
}