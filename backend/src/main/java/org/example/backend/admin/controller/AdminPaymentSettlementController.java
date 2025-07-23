package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.SettlementStatsDto;
import org.example.backend.admin.dto.SettlementTableDto;
import org.example.backend.admin.service.AdminPaymentService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminPaymentSettlementController {
    private final AdminPaymentService adminPaymentService;

    @GetMapping("/statscard")
    public SettlementStatsDto getStats() {
        return adminPaymentService.getStats();
    }

    @GetMapping("/table")
    public List<SettlementTableDto> getSettlementList() {
        return adminPaymentService.getSettlementList();
    }
}
