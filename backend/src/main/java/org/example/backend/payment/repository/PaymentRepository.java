package org.example.backend.payment.repository;

import org.example.backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
  // PK 타입이 String (payId로 저장하셨으므로)
}
