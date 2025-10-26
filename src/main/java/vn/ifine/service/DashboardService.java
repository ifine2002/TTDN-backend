package vn.ifine.service;

import org.springframework.stereotype.Service;
import vn.ifine.dto.response.ResDashboard;

@Service
public interface DashboardService {

  ResDashboard getDataDashboard();
}
