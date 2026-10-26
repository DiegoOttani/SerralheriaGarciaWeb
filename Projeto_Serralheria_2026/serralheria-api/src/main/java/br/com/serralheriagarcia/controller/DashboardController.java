package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.dto.DashboardResponse;
import br.com.serralheriagarcia.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse resumo() {
        return dashboardService.resumo();
    }
}
