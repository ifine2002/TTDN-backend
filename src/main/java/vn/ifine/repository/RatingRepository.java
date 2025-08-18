package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {
  List<Rating> findByBookId(long bookId);

  Optional<Rating> findByIdAndUserId(Long ratingId, Long userId);

  boolean existsByBookIdAndUserId(Long bookId, Long userId);

  int countByBookIdAndStars(Long bookId, long stars);
}
