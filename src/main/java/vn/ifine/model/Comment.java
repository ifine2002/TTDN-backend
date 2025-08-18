package vn.ifine.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AbstractEntity<Long>{

  // Người viết bình luận
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // Bình luận thuộc bài đánh giá nào
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
  private String comment;

  @Column(name = "is_rating_comment")
  private boolean isRatingComment = false;
}
