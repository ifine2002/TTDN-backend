package vn.ifine.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.PermissionRequestDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Permission;
import vn.ifine.model.Role;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.repository.RoleRepository;
import vn.ifine.service.PermissionService;
import vn.ifine.specification.GenericSpecification;

@Service
@Slf4j(topic = "PERMISSION-SERVICE-IMPL")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;

  @Override
  public boolean existsByModuleAndApiPathAndMethod(PermissionRequestDTO p) {
    return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(),
        p.getApiPath(),
        p.getMethod());
  }

  @Override
  public Permission create(PermissionRequestDTO p) {
    Permission permission = Permission.builder()
        .name(p.getName())
        .apiPath(p.getApiPath())
        .method(p.getMethod())
        .module(p.getModule())
        .build();
    permission = permissionRepository.save(permission);
    log.info("Permission has been created successfully, permissionId={}", permission.getId());
    return permission;
  }

  @Override
  public Permission getById(long id) {
    return permissionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id = " + id));
  }

  @Override
  public Permission update(long permissionId, PermissionRequestDTO permission) {
    Permission permissionDB = this.getById(permissionId);
    permissionDB.setName(permission.getName());
    permissionDB.setApiPath(permission.getApiPath());
    permissionDB.setMethod(permission.getMethod());
    permissionDB.setModule(permission.getModule());

    // update
    permissionDB = permissionRepository.save(permissionDB);
    log.info("Permission has updated successfully, permissionId={}", permissionDB.getId());
    return permissionDB;
  }

  @Override
  public void deleteSoft(long id) {
    Permission permissionDB = this.getById(id);
    permissionDB.setIsActive(false);

    // update
    permissionDB = permissionRepository.save(permissionDB);
    log.info("Permission has delete-soft successfully, permissionId={}", permissionDB.getId());
  }

  @Override
  public void remove(long id) {
    // delete permission_role
    Permission p = this.getById(id);
    p.getRoles().forEach(role -> role.getPermissions().remove(p));
    // delete permission
    permissionRepository.delete(p);
    log.info("Permission has delete-soft successfully, permissionId={}", id);
  }

  @Override
  public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
    Page<Permission> pagePermission = permissionRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pagePermission.getTotalPages());
    rs.setTotalElements(pagePermission.getTotalElements());

    rs.setResult(pagePermission.getContent());

    return rs;
  }

  @Override
  public boolean isSameName(long permissionId, PermissionRequestDTO p) {
    Permission permissionDB = this.getById(permissionId);
    if (permissionDB.getName().equals(p.getName())) {
      return true;
    }
    return false;
  }

  @Override
  public void changeIsActive(long id) {
    Permission permissionDB = this.getById(id);
    permissionDB.setIsActive(true);

    // update
    permissionDB = permissionRepository.save(permissionDB);
    log.info("Permission has change isActive successfully, permissionId={}",
        permissionDB.getId());
  }

  @Override
  public ResultPaginationDTO getActivePermissions(Specification<Permission> spec,
      Pageable pageable) {
    // Kết hợp điều kiện isActive với các điều kiện khác
    Specification<Permission> activeSpec = GenericSpecification.withFilter(spec);

    Page<Permission> pagePermission = permissionRepository.findAll(activeSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pagePermission.getTotalPages());
    rs.setTotalElements(pagePermission.getTotalElements());
    rs.setResult(pagePermission.getContent());

    return rs;
  }

  /**
   * manual synchronization logic
   *
   * @param requestedRoles
   * @param permission
   * @return
   */
  private List<Role> resolveRolesFromRequest(List<Role> requestedRoles, Permission permission) {
    if (requestedRoles == null || requestedRoles.isEmpty()) {
      return Collections.emptyList();
    }

    List<Integer> requestRoleIds = requestedRoles.stream()
        .map(Role::getId)
        .toList();

    Set<Integer> roleIds = new HashSet<>(requestRoleIds);

    List<Role> dbRoles = roleRepository.findByIdIn(requestRoleIds);

    if (dbRoles.size() != roleIds.size()) {
      Set<Integer> existingRoleIds = dbRoles.stream()
          .map(Role::getId)
          .collect(Collectors.toSet());

      List<Integer> nonExistentRoleIds = roleIds.stream()
          .filter(id -> !existingRoleIds.contains(id))
          .toList();

      throw new ResourceNotFoundException("Following roles do not exist: " + nonExistentRoleIds);
    }

    // Đồng bộ 2 chiều
    for (Role role : dbRoles) {
      if (!role.getPermissions().contains(permission)) {
        role.getPermissions().add(permission);
      }
    }

    return dbRoles;
  }
}
