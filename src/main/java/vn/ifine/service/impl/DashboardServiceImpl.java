package vn.ifine.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ifine.dto.DailyNewBooksDto;
import vn.ifine.dto.response.ResDashboard;
import vn.ifine.repository.BookRepository;
import vn.ifine.repository.RatingRepository;
import vn.ifine.repository.UserRepository;
import vn.ifine.service.DashboardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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

  @Override
  public List<DailyNewBooksDto> getDailyNewBooks(LocalDate from, LocalDate to) {
    List<Object[]> rows = bookRepository.findDailyNewBooks(from.toString(), to.toString());
    return rows.stream()
            .map(r -> {
              // r[0] -> java.sql.Date or String, r[1] -> Number
              LocalDate day;
              if (r[0] instanceof Date) {
                day = ((Date) r[0]).toLocalDate();
              } else if (r[0] instanceof java.sql.Timestamp) {
                day = ((java.sql.Timestamp) r[0]).toLocalDateTime().toLocalDate();
              } else {
                day = LocalDate.parse(r[0].toString());
              }
              long cnt = r[1] == null ? 0L : ((Number) r[1]).longValue();
              return new DailyNewBooksDto(day, cnt);
            })
            .toList();
  }
}
