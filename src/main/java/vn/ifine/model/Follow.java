package vn.ifine.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "follows")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends AbstractEntity<Long>{

  // Người theo dõi
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  // Người được theo dõi
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "following_id", nullable = false)
  private User following;

}
