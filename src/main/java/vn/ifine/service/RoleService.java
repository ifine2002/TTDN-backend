package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.RoleRequestDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Role;

@Service
public interface RoleService {
  boolean existByName(String name);

  Role create(RoleRequestDTO role);

  Role getById(int id);

  Role update(int id, RoleRequestDTO role);

  void deleteSoft(int id);

  void remove(int id);

  void changeIsActive(int id);

  ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable);

  ResultPaginationDTO getActiveRoles(Specification<Role> spec, Pageable pageable);
}
