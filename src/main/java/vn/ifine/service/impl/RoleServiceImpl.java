package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqRoleDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Permission;
import vn.ifine.model.Role;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.repository.RoleRepository;
import vn.ifine.service.RoleService;
import vn.ifine.specification.GenericSpecification;

@Service
@Slf4j(topic = "ROLE-SERVICE-IMPL")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Override
  public boolean existByName(String name) {
    return roleRepository.existsByName(name);
  }

  @Override
  public Role create(ReqRoleDTO role) {
    Role roleCurrent = Role.builder()
        .name(role.getName())
        .description(role.getDescription())
        .build();
    // check permissions
    if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
      List<Long> reqPermissions = role.getPermissions().stream().map(x -> x.getId()).toList();

      List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
      roleCurrent.setPermissions(dbPermissions);
    }
    log.info("Role has been created successfully, roleId={}", roleCurrent.getId());
    return this.roleRepository.save(roleCurrent);
  }

  @Override
  public Role getById(int id) {
    return roleRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Role not found with id = " + id));
  }

  @Override
  public Role update(int id, ReqRoleDTO role) {
    Role dbRole = this.getById(id);
    dbRole.setName(role.getName());
    dbRole.setDescription(role.getDescription());
    dbRole.setIsActive(role.isActive());
    // check permission
    if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
      List<Long> reqPermisssions = role.getPermissions().stream().map(x -> x.getId())
          .toList();
      List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermisssions);
      dbRole.setPermissions(dbPermissions);
    }
    // update
    dbRole = roleRepository.save(dbRole);
    log.info("Role has been updated successfully, roleId={}", dbRole.getId());
    return dbRole;
  }

  @Override
  public void remove(int id) {
    // delete role inside user
    Role role = this.getById(id);
    role.getUsers().forEach(user -> user.setRole(null));
    this.roleRepository.deleteById(id);
    log.info("Role has remove successfully, roleId={}", role.getId());
  }

  @Override
  public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
    Page<Role> pageRole = roleRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageRole.getTotalPages());
    rs.setTotalElements(pageRole.getTotalElements());
    rs.setResult(pageRole.getContent());

    return rs;
  }
}