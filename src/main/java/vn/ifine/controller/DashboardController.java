package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResDashboard;
import vn.ifine.service.DashboardService;

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
}
