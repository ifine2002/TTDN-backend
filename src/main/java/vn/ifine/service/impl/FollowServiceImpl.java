package vn.ifine.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResUserFollow;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.exception.CustomException;
import vn.ifine.exception.ResourceNotFoundException;
import vn.ifine.model.Follow;
import vn.ifine.model.User;
import vn.ifine.repository.FollowRepository;
import vn.ifine.service.FollowService;
import vn.ifine.service.UserService;

@Service
@Slf4j(topic = "FOLLOW-SERVICE-IMPL")
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

  private final FollowRepository followRepository;
  private final UserService userService;

  @Override
  public ResFollowDTO create(Long followerId, Long followingId) {
    log.info("Request create follow, followerId={}, followingId={}", followerId, followingId);
    User follower = userService.getById(followerId);
    if (follower.getId() == followingId) {
      throw new CustomException("Can't follow myself");
    }
    User following = userService.getById(followingId);
    Follow follow = Follow.builder()
        .follower(follower)
        .following(following)
        .build();
    follow = followRepository.save(follow);
    return this.convertToResFollowDTO(follow);
  }

  @Override
  public void delete(Long id) {
    log.info("Request delete follow, id={}", id);
    Follow follow = followRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Not found follow with id = " + id));
    followRepository.delete(follow);
  }

  @Override
  public void unfollow(Long followerId, Long followingId) {
    Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Not found follow with followerId = " + followerId + " and followingId = "
                + followingId));
    followRepository.delete(follow);
  }

  @Override
  public ResultPaginationDTO getFollows(Specification<Follow> spec, Pageable pageable) {
    Page<Follow> pageFollow = followRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pageFollow.getTotalPages());
    rs.setTotalElements(pageFollow.getTotalElements());

    // convert data
    List<ResFollowDTO> listUser = pageFollow.getContent()
        .stream().map(this::convertToResFollowDTO)
        .toList();
    rs.setResult(listUser);
    return rs;
  }

  @Override
  public ResultPaginationDTO getListFollowing(String email, Specification<Follow> spec,
      Pageable pageable) {

    User user = userService.getUserByEmail(email);

    // Tạo specification để lọc theo userId
    Specification<Follow> userSpec = (root, query, cb) -> cb.equal(root.get("follower").get("id"), user.getId());

    // Kết hợp với specification được truyền vào
    Specification<Follow> combinedSpec = userSpec.and(spec);

    Page<Follow> pageFollow = followRepository.findAll(combinedSpec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();

    rs.setPage(pageable.getPageNumber() + 1);
    rs.setPageSize(pageable.getPageSize());

    rs.setTotalPages(pageFollow.getTotalPages());
    rs.setTotalElements(pageFollow.getTotalElements());

    // convert data
    List<ResUserFollow> listUser = pageFollow.getContent()
        .stream().map(follow -> {
          User userFollow = follow.getFollowing();
          return this.convertToResUserFollow(userFollow);
        })
        .toList();
    rs.setResult(listUser);
    return rs;
  }

  private ResFollowDTO convertToResFollowDTO(Follow follow) {
    return ResFollowDTO.builder()
        .id(follow.getId())
        .followerId(follow.getFollower().getId())
        .followingId(follow.getFollowing().getId())
        .createdAt(follow.getCreatedAt())
        .createdBy(follow.getCreatedBy())
        .build();
  }

  private ResUserFollow convertToResUserFollow(User user) {
    return new ResUserFollow(user.getId(), user.getFullName(), user.getImage());
  }
}
