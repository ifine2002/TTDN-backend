package vn.ifine.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResLoginDTO;

@Service
public interface JwtService {

  String createAccessToken(String email, ResLoginDTO dto);

  String createRefreshToken(String email, ResLoginDTO dto);

  Jwt checkValidRefreshToken(String token);
}
