package vn.ifine.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ifine.dto.request.ReqCommentDTO;
import vn.ifine.dto.request.ReqRatingDTO;
import vn.ifine.dto.request.ReviewRequestDto;
import vn.ifine.dto.response.ResComment;
import vn.ifine.dto.response.ResRatingDTO;
import vn.ifine.dto.response.ResReviewDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.CustomException;
import vn.ifine.exception.ResourceAlreadyExistsException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Book;
import vn.ifine.model.Comment;
import vn.ifine.model.Rating;
import vn.ifine.model.User;
import vn.ifine.repository.CommentRepository;
import vn.ifine.repository.RatingRepository;
import vn.ifine.service.BookService;
import vn.ifine.service.ReviewService;
import vn.ifine.service.UserService;
import vn.ifine.service.WebSocketService;

@Service
@Slf4j(topic = "REVIEW-SERVICE-IMPL")
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final RatingRepository ratingRepository;
  private final CommentRepository commentRepository;
  private final UserService userService;
  private final WebSocketService webSocketService;
  private final BookService bookService;

  @Override
  public ResRatingDTO createRating(ReqRatingDTO request) {
    if (ratingRepository.existsByBookIdAndUserId(request.getBookId(), request.getUserId())) {
      throw new ResourceAlreadyExistsException(
          "User with id = " + request.getUserId() + " has rated book with id = "
              + request.getBookId());
    }
    User user = userService.getById(request.getUserId());
    Book book = bookService.getById(request.getBookId());
    Rating rating = Rating.builder()
        .user(user)
        .book(book)
        .stars(request.getStars())
        .build();
    rating = ratingRepository.save(rating);
    log.info("Rating has been created success, ratingId={}", rating.getId());
    return this.convertToResRatingDTO(rating);
  }

  @Override
  public ResRatingDTO updateRating(Long id, Long stars) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found rating with id = " + id));
    rating.setStars(stars);
    rating = ratingRepository.save(rating);
    log.info("Rating has been updated success, ratingId={}", rating.getId());
    return this.convertToResRatingDTO(rating);
  }

  @Override
  public void deleteRating(Long id) {
    Rating rating = ratingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found rating with id = " + id));
    ratingRepository.delete(rating);
    log.info("Rating has been deleted success, ratingId={}", rating.getId());
  }

  @Override
  public ResultPaginationDTO getRatings(Specification<Rating> spec, Pageable pageable) {
    Page<Rating> pageRating = ratingRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageRating.getTotalPages());
    rs.setTotalElements(pageRating.getTotalElements());
    List<ResRatingDTO> listRating = pageRating.getContent()
        .stream().map(this::convertToResRatingDTO)
        .toList();
    rs.setResult(listRating);
    return rs;
  }

  @Override
  public ResComment createComment(ReqCommentDTO request) {
    User user = userService.getById(request.getUserId());
    Book book = bookService.getById(request.getBookId());
    Comment comment = Comment.builder()
        .comment(request.getComment())
        .book(book)
        .user(user)
        .isRatingComment(request.isRatingComment())
        .build();
    comment = commentRepository.save(comment);
    log.info("Comment has been created success, commentId={}", comment.getId());
    return this.convertToResComment(comment);
  }

  @Override
  public ResComment updateComment(Long id, ReqCommentDTO request) {
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found comment with id = " + id));
    comment.setComment(request.getComment());
    comment.setRatingComment(request.isRatingComment());
    comment = commentRepository.save(comment);

    log.info("Comment has been updated success, commentId={}", comment.getId());
    return this.convertToResComment(comment);
  }

  @Override
  public void deleteComment(Long id) {
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found comment with id = " + id));
    commentRepository.delete(comment);
    log.info("Comment has been deleted success, commentId={}", comment.getId());
  }

  @Override
  public ResultPaginationDTO getComments(Specification<Comment> spec, Pageable pageable) {
    Page<Comment> pageComment = commentRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());
    rs.setTotalPages(pageComment.getTotalPages());
    rs.setTotalElements(pageComment.getTotalElements());
    List<ResComment> listComment = pageComment.getContent()
        .stream().map(this::convertToResComment)
        .toList();
    rs.setResult(listComment);
    return rs;
  }

  @Override
  @Transactional
  public void createReview(Long bookId, ReviewRequestDto request, String email) {
    log.info("Request create review commentId, bookId={}, emailUser={}", bookId, email);
    User user = userService.getUserByEmail(email);
    Book book = bookService.getById(bookId);

    boolean hasRating = request.getStars() != null;
    boolean hasComment = request.getComment() != null && !request.getComment().isBlank();

    if (!hasRating) {
      throw new IllegalArgumentException("Must have at least one star rating");
    }

    Rating rating = new Rating();
    rating.setStars(request.getStars());
    rating.setBook(book);
    rating.setUser(user);
    ratingRepository.save(rating);

    Comment comment = new Comment();
    comment.setBook(book);
    comment.setUser(user);
    comment.setComment(request.getComment());
    comment.setRatingComment(true);

    if (hasComment) {
      comment = commentRepository.save(comment);
    }

    //Gửi WebSocket comment sau khi lưu
    ResReviewDTO res = new ResReviewDTO();
    res.setFullName(user.getFullName());
    res.setUserId(user.getId());
    res.setImage(user.getImage());
    res.setStars(rating.getStars());
    res.setRatingId(rating.getId());
    res.setCreatedAt(rating.getCreatedAt());
    res.setUpdatedAt(rating.getUpdatedAt());
    if (hasComment) {
      res.setCommentId(comment.getId());
      res.setComment(comment.getComment());
    }
    webSocketService.sendReviewNotification("create", bookId, res);
  }

  @Override
  @Transactional
  public void updateReview(Long commentId, Long ratingId, ReviewRequestDto request, String email) {
    log.info("Request update review commentId, commentId={}, ratingId={}", commentId, ratingId);
    User user = userService.getUserByEmail(email);
    boolean hasComment = request.getComment() != null && !request.getComment().isBlank(); //tồn tại
    if (request.getStars() == null && (request.getComment() == null || request.getComment()
        .isBlank())) {
      throw new IllegalArgumentException("Must have at least one star rating or comment");
    }

    Rating rating = ratingRepository.findByIdAndUserId(ratingId, user.getId()).orElseThrow(() ->
        new ResourceNotFoundException(
            "Not found rating with ratingId=" + ratingId + " and userId=" + user.getId()));

    rating.setStars(request.getStars());
    ratingRepository.save(rating);

    Comment comment = new Comment();

    if(!hasComment && commentId != null){
       comment = commentRepository.findByIdAndUserId(commentId, user.getId()).orElseThrow(
          () -> new ResourceNotFoundException(
              "Not found comment with commentId = " + commentId + " and userId=" + user.getId()));
      comment.setComment(request.getComment());
      commentRepository.delete(comment);
    } else if(hasComment) {
      comment = commentRepository.findByIdAndUserIdAndIsRatingCommentTrue(commentId, user.getId()).orElse(new Comment());
      comment.setBook(rating.getBook());
      comment.setUser(user);
      comment.setComment(request.getComment());
      comment.setRatingComment(true);

      comment = commentRepository.save(comment);
    }
    // Gửi WebSocket comment sau khi lưu
    ResReviewDTO res = new ResReviewDTO();
    res.setFullName(user.getFullName());
    res.setUserId(user.getId());
    res.setImage(user.getImage());
    res.setStars(rating.getStars());
    res.setRatingId(rating.getId());
    res.setCreatedAt(rating.getCreatedAt());
    res.setUpdatedAt(rating.getUpdatedAt());
    if (hasComment) {
      res.setCommentId(comment.getId());
      res.setComment(comment.getComment());
    } else {
      res.setCommentId(null);
      res.setComment(null);
    }
    webSocketService.sendReviewNotification("update", rating.getBook().getId(), res);
  }

  @Override
  @Transactional
  public void deleteReview(Long commentId, Long ratingId, String email) {
    log.info("Request delete review commentId={}, ratingId={}, emailUser={}", commentId, ratingId,
        email);
    User user = userService.getUserByEmail(email);
    // Xóa rating
    Optional<Rating> ratingOptional = ratingRepository.findById(ratingId);
    if (ratingOptional.isEmpty()) {
      throw new ResourceNotFoundException("Rating not found with id: " + ratingId);
    }
    Rating rating = ratingOptional.get();
    // Kiểm tra quyền xóa (chỉ cho phép người tạo xóa)
    if (!rating.getUser().getId().equals(user.getId())) {
      throw new CustomException("You don't have permission to delete this rating");
    }
    Book book = rating.getBook();
    Long bookId = book.getId();
    // Xóa comment nếu có
    if (commentId != null) {
      Optional<Comment> commentOptional = commentRepository.findById(commentId);
      if (commentOptional.isPresent()) {
        Comment comment = commentOptional.get();
        // Kiểm tra quyền xóa (chỉ cho phép người tạo xóa)
        if (!comment.getUser().getId().equals(user.getId())) {
          throw new CustomException("You don't have permission to delete this comment");
        }
        commentRepository.delete(comment);
      }
    }
    // Xóa rating
    ratingRepository.delete(rating);
    // Gửi thông báo WebSocket
    webSocketService.sendReviewNotification("delete", bookId, user.getId());
  }

  private ResComment convertToResComment(Comment comment) {
    return ResComment.builder()
        .id(comment.getId())
        .comment(comment.getComment())
        .isRatingComment(comment.isRatingComment())
        .userId(comment.getUser().getId())
        .bookId(comment.getBook().getId())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .createdBy(comment.getCreatedBy())
        .updatedBy(comment.getUpdatedBy())
        .build();
  }

  private ResRatingDTO convertToResRatingDTO(Rating rating) {
    return ResRatingDTO.builder()
        .id(rating.getId())
        .userId(rating.getUser().getId())
        .bookId(rating.getBook().getId())
        .stars(rating.getStars())
        .createdAt(rating.getCreatedAt())
        .updatedAt(rating.getUpdatedAt())
        .createdBy(rating.getCreatedBy())
        .updatedBy(rating.getUpdatedBy())
        .build();
  }
}
