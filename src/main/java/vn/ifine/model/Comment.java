package vn.ifine.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // Bình luận thuộc bài đánh giá nào
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Book book;

  @Column(name = "comment", columnDefinition = "TEXT", nullable = false)
  private String comment;

  @Column(name = "is_rating_comment")
  private boolean isRatingComment = false;
}
