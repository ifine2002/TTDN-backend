package vn.ifine.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResDashboard;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.RatingRepository;
import vn.ifine.repository.UserRepository;
import vn.ifine.service.DashboardService;

@Slf4j(topic = "DASHBOARD-SERVICE-IMPL")
@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService {

  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final RatingRepository ratingRepository;

  @Override
  public ResDashboard getDataDashboard() {
    long totalUser = this.userRepository.count();
    long totalBook = this.bookRepository.count();
    long totalReview = this.ratingRepository.count();

    ResDashboard res = ResDashboard.builder()
        .totalUser(totalUser)
        .totalBook(totalBook)
        .totalReview(totalReview)
        .build();
    return res;
  }
}
