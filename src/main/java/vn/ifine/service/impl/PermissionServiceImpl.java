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
import vn.ifine.dto.request.ReqPermissionDTO;
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
  public boolean existsByModuleAndApiPathAndMethod(ReqPermissionDTO p) {
    return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(),
        p.getApiPath(),
        p.getMethod());
  }

  @Override
  public Permission create(ReqPermissionDTO p) {
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
  public Permission update(long permissionId, ReqPermissionDTO permission) {
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
  public boolean isSameName(long permissionId, ReqPermissionDTO p) {
    Permission permissionDB = this.getById(permissionId);
    if (permissionDB.getName().equals(p.getName())) {
      return true;
    }
    return false;
  }
}
