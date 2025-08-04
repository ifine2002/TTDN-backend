package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
import vn.ifine.dto.request.ReqCreateUser;
import vn.ifine.dto.request.ReqUpdateUser;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.dto.response.UserResponse;
import vn.ifine.model.Role;
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

  @PostMapping("/")
  public ResponseEntity<ApiResponse<UserResponse>> create(
      @Valid @RequestBody ReqCreateUser reqUser) {
    log.info("Request add user, {} {}", reqUser.getEmail(), reqUser.getFullName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a user success",
            this.userService.createUser(reqUser)));
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

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable @Min(1) long id,
      @Valid @RequestBody ReqUpdateUser reqUser) {
    log.info("Request update user, id={}", id);
    UserResponse user = userService.update(id, reqUser);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a user success",
            user));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") long id) {
    log.info("Request remove user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a user success", null));
  }

  // Delete soft
  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteSoft(@PathVariable @Min(1) long id) {
    log.info("Request delete-soft user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.changeStatus(id, UserStatus.DELETED);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Delete-soft a user success", null));
  }

  // Change status: ACTIVE
  @PatchMapping("/changeIsActive/{id}")
  public ResponseEntity<ApiResponse<Void>> changeIsActive(@PathVariable @Min(1) long id) {
    log.info("Request change isActive user, id={}", id);
    // check exist by id
    User user = this.userService.getById(id);

    this.userService.changeStatus(id, UserStatus.ACTIVE);
    return ResponseEntity.ok()
        .body(ApiResponse.success("User is active success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getPermissions(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all user success",
            this.userService.getAll(spec, pageable)));
  }

  @GetMapping("/active")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getActivePermissions(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all user active success",
            this.userService.getAllActive(spec, pageable)));
  }

  // Update role
  @PatchMapping("/change-role/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> changeRole(@PathVariable @Min(1) long userId,
      @RequestParam @Min(1) int roleId) {
    log.info("Request update role-user, userId={}", userId);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update role for user success",
            this.userService.changeRole(userId, roleId)));
  }
}