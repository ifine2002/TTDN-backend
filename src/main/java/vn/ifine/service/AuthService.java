package vn.ifine.service;

import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqLoginDTO;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.dto.response.ResUserAccount;

@Service
public interface AuthService {

  ResLoginDTO login(ReqLoginDTO loginDTO);

  ResLoginDTO getRefreshToken(String refresh_token);

  void register(ReqRegisterDTO registerDTO);

  void verifyToken(String token);

  ResUserAccount getAccount();
}
