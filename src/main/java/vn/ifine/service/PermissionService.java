package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.PermissionRequestDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Permission;

@Service
public interface PermissionService {

  boolean existsByModuleAndApiPathAndMethod(PermissionRequestDTO p);

  Permission create(PermissionRequestDTO p);

  Permission getById(long id);

  Permission update(long permissionId, PermissionRequestDTO permission);

  void deleteSoft(long id);

  void remove(long id);

  ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable);

  boolean isSameName(long permissionId, PermissionRequestDTO p);

  void changeIsActive(long id);

  ResultPaginationDTO getActivePermissions(Specification<Permission> spec, Pageable pageable);
}
