package vn.ifine.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ifine.dto.JwtInfo;
import vn.ifine.repository.RedisTokenRepository;
import vn.ifine.service.JwtService;
import vn.ifine.service.RedisService;

@Service
@Slf4j(topic = "REDIS-SERVICE-IMPL")
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

  private final RedisTokenRepository redisTokenRepository;
  private final JwtService jwtService;

  @Override
  public boolean isBlacklisted(String token) {
    JwtInfo jwtInfo = jwtService.parseToken(token);
    return redisTokenRepository.existsById(jwtInfo.getJwtId());
  }
}
