package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.DashDailySaleDto;
import org.example.backend.admin.repository.DashDailySaleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashDailySaleService {
    private final DashDailySaleRepository dashDailySaleRepository;

    public List<DashDailySaleDto> getRecentReports(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return dashDailySaleRepository.findRecentReports(pageable);
    }
}
