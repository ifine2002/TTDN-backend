package vn.ifine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.ifine.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
  List<Comment> findByBookIdAndIsRatingCommentFalseOrderByUpdatedAtDesc(long bookId);

  List<Comment> findByBookIdAndIsRatingCommentTrue(long bookId);

  Optional<Comment> findByIdAndUserId(Long commentId, Long userId);

  Optional<Comment> findByIdAndUserIdAndIsRatingCommentTrue(Long bookId, Long userId);
}
