package vn.ifine.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Follow;
import vn.ifine.service.FollowService;

@RestController
@RequestMapping("/follow")
@Validated
@Slf4j(topic = "FOLLOW-CONTROLLER")
@Tag(name = "Follow Controller")
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<ResFollowDTO>> followUser(
      @RequestParam @Min(1) Long followerId, @RequestParam @Min(1) Long followingId) {
    log.info("Request create follow, followerId={}, followingId={}", followerId,
        followingId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Follow user success",
            this.followService.create(followerId, followingId)));
  }

  // Remove
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
    log.info("Request delete follow, id={}", id);
    this.followService.delete(id);
    return ResponseEntity.ok().body(ApiResponse.success("Delete follow success", null));
  }

  @DeleteMapping("/")
  public ResponseEntity<ApiResponse<Void>> unfollow(@RequestParam @Min(1) Long followerId,
      @RequestParam @Min(1) Long followingId) {
    log.info("Request unfollow, followerId={}, followingId={}", followerId, followingId);
    this.followService.unfollow(followerId, followingId);
    return ResponseEntity.ok().body(ApiResponse.success("Unfollow success", null));
  }

  @GetMapping("/list")
  public ResponseEntity<ApiResponse<ResultPaginationDTO>> getFollows(
      @Filter Specification<Follow> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Fetch all follow success",
            this.followService.getFollows(spec, pageable)));
  }

  // Lấy danh sách user bạn đang follow
  @GetMapping("/list-following")
  public ResponseEntity<ApiResponse<?>> getListFollowing(
      Principal principal, @Filter Specification<Follow> spec,
      Pageable pageable) {
    return ResponseEntity.ok().body(
        ApiResponse.success("Get list following success",
            this.followService.getListFollowing(principal.getName(), spec, pageable)));
  }
}
