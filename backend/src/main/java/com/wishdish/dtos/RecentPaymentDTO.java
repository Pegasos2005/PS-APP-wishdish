package com.wishdish.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecentPaymentDTO {

    private Long id;
    private Integer tableNumber;
    private BigDecimal amount;
    private LocalDateTime paidAt;

    public RecentPaymentDTO() {
    }

    public RecentPaymentDTO(Long id, Integer tableNumber, BigDecimal amount, LocalDateTime paidAt) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
