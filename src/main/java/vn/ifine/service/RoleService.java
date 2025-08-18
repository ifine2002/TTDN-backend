package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqRoleDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Role;

@Service
public interface RoleService {
  boolean existByName(String name);

  Role create(ReqRoleDTO role);

  Role getById(int id);

  Role update(int id, ReqRoleDTO role);

  void remove(int id);

  ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable);
}
