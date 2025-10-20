package vn.ifine.model;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RedisToken")
public class RedisToken {

  @Id
  private String jwtId;

  @TimeToLive(unit = TimeUnit.MILLISECONDS)
  private Long expiredTime;


}
