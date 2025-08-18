package vn.ifine.service;

import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqLoginDTO;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.request.ReqResetPassword;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.dto.response.ResUserAccount;
import vn.ifine.model.User;

@Service
public interface AuthService {

  ResLoginDTO login(ReqLoginDTO loginDTO);

  ResLoginDTO getRefreshToken(String refresh_token);

  void register(ReqRegisterDTO registerDTO);

  void verifyToken(String email, String token);

  ResUserAccount getAccount();

  void createAndSendToken(User user);

  void resetPassword(String token, ReqResetPassword request);

  void sendTokenResetPassword(String email);
}
