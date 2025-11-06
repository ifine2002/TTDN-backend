package vn.ifine.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
public class VerificationToken implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Long id;

  private String token;

  @OneToOne
  private User user;

  private LocalDateTime expiryDate;

  @Column(name = "created_at")
  @CreationTimestamp
  private Date createdAt;
}
