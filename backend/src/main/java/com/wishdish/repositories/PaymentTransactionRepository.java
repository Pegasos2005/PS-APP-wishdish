package com.wishdish.repositories;

import com.wishdish.models.PaymentStatus;
import com.wishdish.models.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<PaymentTransaction> findByStatusAndUpdatedAtAfterOrderByUpdatedAtDesc(
            PaymentStatus status, LocalDateTime after);
}
