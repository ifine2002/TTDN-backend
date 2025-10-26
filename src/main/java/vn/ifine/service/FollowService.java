package vn.ifine.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResFollowDTO;
import vn.ifine.dto.response.ResultPaginationDTO;
import vn.ifine.model.Follow;

@Service
public interface FollowService {

  ResFollowDTO create(Long followerId, Long followingId);

  void delete(Long id);

  void unfollow(Long followerId, Long followingId);

  ResultPaginationDTO getFollows(Specification<Follow> spec, Pageable pageable);

  //List user bạn đang follow
  ResultPaginationDTO getListFollowing(String email, Specification<Follow> spec, Pageable pageable);
}
