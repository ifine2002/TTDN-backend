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
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqRoleDTO;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.model.Role;
import vn.ifine.service.RoleService;

@RestController
@RequestMapping("/role")
@Slf4j(topic = "ROLE-CONTROLLER")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role Controller")
public class RoleController {

  private final RoleService roleService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<Role>> create(
      @Valid @RequestBody ReqRoleDTO role) {
    log.info("Request add role, {}", role.getName());
    // check name
    if (this.roleService.existByName(role.getName())) {
      throw new ResourceAlreadyExistsException(
          "Role with name = " + role.getName() + " already exists");
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create a role success",
            this.roleService.create(role)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable @Min(1) int id) {
    log.info("Request get role, id={}", id);
    // check exist by id
    Role existingRole = this.roleService.getById(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch a role success",
            existingRole));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Role>> update(@PathVariable @Min(1) int id,
      @Valid @RequestBody ReqRoleDTO reqRole) {
    log.info("Request update role, id={}", id);
    //check exist by id
    Role role = roleService.getById(id);
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a role success",
            roleService.update(id, reqRole)));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> remove(@PathVariable("id") int id) {
    log.info("Request remove role, id={}", id);
    // check exist by id
    Role role = roleService.getById(id);
    this.roleService.remove(id);
    return ResponseEntity.ok().body(ApiResponse.success("Remove a role success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getPermissions(
      @Filter Specification<Role> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all role success",
            this.roleService.getRoles(spec, pageable)));
  }
}
