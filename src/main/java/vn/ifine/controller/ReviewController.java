package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqCommentDTO;
import vn.ifine.dto.request.ReqRatingDTO;
import vn.ifine.dto.request.ReviewRequestDto;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResComment;
import vn.ifine.dto.response.ResRatingDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Comment;
import vn.ifine.model.Rating;
import vn.ifine.service.ReviewService;

@RestController
@RequestMapping("/review")
@Slf4j(topic = "REVIEW-CONTROLLER")
@RequiredArgsConstructor
@Validated
@Tag(name = "Review Controller")
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping("/rating")
  public ResponseEntity<ApiResponse<ResRatingDTO>> createRating(
      @RequestBody @Valid ReqRatingDTO request) {
    log.info("Request create rating , {}", request.getStars());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create rating success",
            reviewService.createRating(request)));
  }

  @PutMapping("/rating/{id}")
  public ResponseEntity<ApiResponse<ResRatingDTO>> updateRating(
      @PathVariable("id") @Min(1) Long id, @RequestParam long stars) {
    log.info("Request update rating , {}", stars);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Update rating success",
            reviewService.updateRating(id, stars)));
  }

  @DeleteMapping("/rating/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteRating(
      @PathVariable("id") @Min(1) Long id) {
    log.info("Request delete rating , id={}", id);
    reviewService.deleteRating(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Delete rating success",
            null));
  }

  @GetMapping("/rating/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getListRating(
      @Filter Specification<Rating> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all rating success",
            this.reviewService.getRatings(spec, pageable)));
  }

  @PostMapping("/comment")
  public ResponseEntity<ApiResponse<ResComment>> createComment(
      @RequestBody @Valid ReqCommentDTO request) {
    log.info("Request create comment , {}", request.getComment());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create comment success",
            reviewService.createComment(request)));
  }

  @PutMapping("/comment/{id}")
  public ResponseEntity<ApiResponse<ResComment>> updateComment(
      @PathVariable("id") @Min(1) Long id, @RequestBody @Valid ReqCommentDTO request) {
    log.info("Request update comment , {}", request.getComment());
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Update comment success",
            reviewService.updateComment(id, request)));
  }

  @DeleteMapping("/comment/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteComment(
      @PathVariable("id") @Min(1) Long id) {
    log.info("Request delete comment , id={}", id);
    reviewService.deleteComment(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse.success("Delete comment success",
            null));
  }

  @GetMapping("/comment/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getListComment(
      @Filter Specification<Comment> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all comment success",
            this.reviewService.getComments(spec, pageable)));
  }

  @PostMapping("/{bookId}")
  public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable @Min(1) long bookId,
      @RequestBody ReviewRequestDto request, Principal principal) {
    log.info("Request comment , {}", request.getComment());
    reviewService.createReview(bookId, request, principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Create comment book success",
            null));
  }

  @PutMapping("/update-review")
  public ResponseEntity<ApiResponse<Void>> updateReview(
      @RequestParam(required = false) @Min(1) Long commentId,
      @RequestParam(required = false) @Min(1) Long ratingId,
      @RequestBody ReviewRequestDto request, Principal principal) {
    log.info("Request update review, commentId={}, ratingId={}", commentId, ratingId);
    reviewService.updateReview(commentId, ratingId, request, principal.getName());
    return ResponseEntity.ok()
        .body(ApiResponse.success("Update a review success",
            null));
  }

  @DeleteMapping("/")
  public ResponseEntity<ApiResponse<Void>> deleteReview(
      @RequestParam(required = false) @Min(1) Long commentId,
      @RequestParam @Min(1) Long ratingId, Principal principal) {
    log.info("Request remove review, commentId={}, ratingId={}", commentId, ratingId);
    reviewService.deleteReview(commentId, ratingId, principal.getName());
    return ResponseEntity.ok().body(ApiResponse.success("Delete review book success", null));
  }

}
