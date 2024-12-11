package ecostruxure.rate.calculator.controllers;

import ecostruxure.rate.calculator.be.dto.DashboardDTO;
import ecostruxure.rate.calculator.bll.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping()
    public List<DashboardDTO> getDashboard() {
        return dashboardService.getDashboard();
    }
}
