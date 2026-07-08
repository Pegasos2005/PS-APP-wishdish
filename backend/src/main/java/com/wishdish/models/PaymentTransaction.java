package com.wishdish.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions",
        indexes = {
                @Index(name = "idx_pt_intent", columnList = "stripe_payment_intent_id", unique = true)
        })
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stripe_payment_intent_id", nullable = false, unique = true, length = 255)
    private String stripePaymentIntentId;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;

    // Instantánea del recibo en JSON, capturada al confirmar el pago
    // (las comandas pasan a "paid" al cerrar la mesa y el ticket activo desaparece).
    // Sin longitud explícita, Hibernate generaría tinytext (255 bytes) y el JSON no cabe.
    @Lob
    @Column(name = "receipt_json", columnDefinition = "longtext")
    private String receiptJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public PaymentTransaction() {
    }

    public Long getId() { return id; }

    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getReceiptJson() { return receiptJson; }
    public void setReceiptJson(String receiptJson) { this.receiptJson = receiptJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
