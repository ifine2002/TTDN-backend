package vn.ifine.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ifine.model.VerificationToken;
import vn.ifine.model.User;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByTokenAndUser(String token, User user);

  Optional<VerificationToken> findByUser(User user);
}
