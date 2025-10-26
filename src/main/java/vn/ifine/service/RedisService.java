package vn.ifine.service;

import org.springframework.stereotype.Service;

@Service
public interface RedisService {

  boolean isBlacklisted(String token);
}
