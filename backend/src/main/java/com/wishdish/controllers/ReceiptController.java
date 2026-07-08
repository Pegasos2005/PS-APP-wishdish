package com.wishdish.controllers;

import com.wishdish.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping(value = "/{reference}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReceipt(@PathVariable String reference) {
        try {
            // La instantánea ya está serializada: se devuelve tal cual como JSON.
            return ResponseEntity.ok(paymentService.getReceiptJson(reference));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
