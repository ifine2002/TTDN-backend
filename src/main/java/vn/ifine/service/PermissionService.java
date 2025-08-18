package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqPermissionDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Permission;

@Service
public interface PermissionService {

  boolean existsByModuleAndApiPathAndMethod(ReqPermissionDTO p);

  Permission create(ReqPermissionDTO p);

  Permission getById(long id);

  Permission update(long permissionId, ReqPermissionDTO permission);

  void remove(long id);

  ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable);

  boolean isSameName(long permissionId, ReqPermissionDTO p);

}
