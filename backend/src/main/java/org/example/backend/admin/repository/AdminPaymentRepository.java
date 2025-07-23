package org.example.backend.admin.repository;

import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminPaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findAllByStatus(PaymentStatus status);
}
