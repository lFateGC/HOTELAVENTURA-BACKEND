package com.hotelaventura.controller;

import com.hotelaventura.dto.DashboardKpiDTO;
import com.hotelaventura.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpiDTO> obtenerKpis() {
        return ResponseEntity.ok(dashboardService.obtenerKpis());
    }
}
