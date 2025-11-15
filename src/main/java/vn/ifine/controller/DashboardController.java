package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.DailyNewBooksDto;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResDashboard;
import vn.ifine.exception.CustomException;
import vn.ifine.service.DashboardService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@Slf4j(topic = "DASHBOARD-CONTROLLER")
@Validated
@Tag(name = "Dashboard Controller")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping()
  public ResponseEntity<ApiResponse<ResDashboard>> dashboard() {
    return ResponseEntity.ok(
        ApiResponse.success("Get statistics successfully.",
            this.dashboardService.getDataDashboard()));
  }

  @GetMapping("/new-books")
  public ResponseEntity<ApiResponse<List<DailyNewBooksDto>>> getNewBooks(
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

    LocalDate end = (to == null) ? LocalDate.now() : to;
    LocalDate start = (from == null) ? end.minusDays(6) : from; // default 7 days
    if (start.isAfter(end)) {
      throw new CustomException("'from' must be <= 'to'");
    }
    return ResponseEntity.ok(
            ApiResponse.success("Get statistics new book successfully.",
                    this.dashboardService.getDailyNewBooks(start, end)));
  }
}
