package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.DashDailySaleDto;
import org.example.backend.admin.repository.DashDailySaleRepository;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashDailySaleService {
    private final DashDailySaleRepository dashDailySaleRepository;

    public List<DashDailySaleDto> getTodayPayments() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return dashDailySaleRepository.findTodayPaidPayments().stream()
                .map(payment -> DashDailySaleDto.builder()
                        .payId(payment.getPayId())
                        .amount(payment.getAmount())
                        .userName(payment.getReservation().getUser().getName())
                        .mentorName(payment.getReservation().getMentor().getUser().getName())
                        .date(payment.getApprovedAt().format(formatter))
                        .build())
                .collect(Collectors.toList());
    }
}
