package vn.ifine.service;

import org.springframework.stereotype.Service;
import vn.ifine.dto.request.UserRequestDTO;
import vn.ifine.model.User;

@Service
public interface UserService {
  User createUser(UserRequestDTO request);
}
