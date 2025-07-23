package org.example.backend.payment.repository;

import org.example.backend.payment.domain.Payment;
import org.example.backend.reservation.domain.MentoringReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

  Optional<Payment> findByPaymentKey(String paymentKey); // ✅ 이 줄 추가
  // PK 타입이 String (payId로 저장하셨으므로)
  Optional<Payment> findByReservation(MentoringReservation reservation);

}
