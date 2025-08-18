package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqCommentDTO;
import vn.ifine.dto.request.ReqRatingDTO;
import vn.ifine.dto.request.ReviewRequestDto;
import vn.ifine.dto.response.ResComment;
import vn.ifine.dto.response.ResRatingDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Comment;
import vn.ifine.model.Permission;
import vn.ifine.model.Rating;

@Service
public interface ReviewService {

  ResRatingDTO createRating(ReqRatingDTO request);

  ResRatingDTO updateRating(Long id, Long stars);

  void deleteRating(Long id);

  ResultPaginationDTO getRatings(Specification<Rating> spec, Pageable pageable);

  ResComment createComment(ReqCommentDTO request);

  ResComment updateComment(Long id, ReqCommentDTO request);

  void deleteComment(Long id);

  ResultPaginationDTO getComments(Specification<Comment> spec, Pageable pageable);

  void createReview(Long bookId, ReviewRequestDto request, String email);

  void updateReview(Long commentId, Long ratingId, ReviewRequestDto request, String email);

  void deleteReview(Long commentId, Long ratingId, String email);

}
