package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, JpaSpecificationExecutor<Follow> {

  Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

  @Query("SELECT f FROM Follow f " +
      "JOIN FETCH f.follower " +
      "WHERE f.following.id = :followingId")
  List<Follow> findByFollowingIdWithFollower(@Param("followingId") Long followingId);

  @Query("SELECT f FROM Follow f " +
      "JOIN FETCH f.following " +
      "WHERE f.follower.id = :followerId")
  List<Follow> findByFollowerIdWithFollowing(@Param("followerId") Long followerId);

  Optional<Follow> findById(Long id);

  Long countByFollowingId(Long userId);

  Long countByFollowerId(Long userId);
}
