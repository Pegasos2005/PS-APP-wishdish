package com.wishdish.controllers;

import com.stripe.exception.StripeException;
import com.wishdish.dtos.ConfirmPaymentRequestDTO;
import com.wishdish.dtos.CreateIntentRequestDTO;
import com.wishdish.dtos.CreateIntentResponseDTO;
import com.wishdish.dtos.RecentPaymentDTO;
import com.wishdish.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createIntent(@RequestBody CreateIntentRequestDTO request) {
        try {
            CreateIntentResponseDTO res = paymentService.createIntent(request.getTableNumber());
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Stripe: " + e.getMessage()));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RecentPaymentDTO>> getRecentPayments() {
        return ResponseEntity.ok(paymentService.getRecentPayments());
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmPaymentRequestDTO request) {
        try {
            paymentService.confirm(request.getPaymentIntentId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Stripe: " + e.getMessage()));
        }
    }
}
