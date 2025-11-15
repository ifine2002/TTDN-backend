package vn.ifine.service;

import org.springframework.stereotype.Service;
import vn.ifine.dto.DailyNewBooksDto;
import vn.ifine.dto.response.ResDashboard;

import java.time.LocalDate;
import java.util.List;

@Service
public interface DashboardService {

  ResDashboard getDataDashboard();

  List<DailyNewBooksDto> getDailyNewBooks(LocalDate from, LocalDate to);
}
