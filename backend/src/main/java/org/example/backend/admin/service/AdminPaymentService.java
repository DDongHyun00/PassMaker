package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.SettlementStatsDto;
import org.example.backend.admin.dto.SettlementTableDto;
import org.example.backend.admin.repository.AdminPaymentRepository;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {
    private final AdminPaymentRepository adminPaymentRepository;

    public SettlementStatsDto getStats() {
        List<Payment> completedPayments = adminPaymentRepository.findAllByStatus(PaymentStatus.PAID);

        long total = completedPayments.stream()
                .mapToLong(Payment::getAmount)
                .sum();

        long mentorShare = Math.round(total * 0.7);
        long adminShare = total - mentorShare;
        long pendingAmount = mentorShare;

        return new SettlementStatsDto(total, mentorShare, adminShare, pendingAmount);
    }

    public List<SettlementTableDto> getSettlementList() {
        List<Payment> payments = adminPaymentRepository.findAllByStatus(PaymentStatus.PAID)
                .stream()
                .filter(p -> p.getReservation() != null && p.getReservation().getMentor() != null)
                .toList();

        // 멘토 ID 기준으로 그룹화
        Map<Long, List<Payment>> groupedByMentor = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getReservation().getMentor().getId()));

        List<SettlementTableDto> result = new ArrayList<>();

        for (Map.Entry<Long, List<Payment>> entry : groupedByMentor.entrySet()) {
            Long mentorId = entry.getKey();
            List<Payment> mentorPayments = entry.getValue();

            String mentorName = mentorPayments.get(0).getReservation().getMentor().getUser().getName();
            int totalPayment = mentorPayments.stream().mapToInt(Payment::getAmount).sum();
            int mentorShare = totalPayment * 70 / 100;
            int adminShare = totalPayment - mentorShare;

            // 지급 상태가 혼합일 수 있지만, 여기서는 "지급완료"로 고정하거나, 대기건이 하나라도 있으면 "지급대기"로 설정 가능
            //boolean hasPending = mentorPayments.stream().anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);
            String status = "지급대기";

            result.add(new SettlementTableDto(
                    mentorId,
                    mentorName,
                    totalPayment,
                    mentorShare,
                    adminShare,
                    status
            ));
        }

        return result;
    }
}
