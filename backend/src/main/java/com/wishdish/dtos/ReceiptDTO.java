package com.wishdish.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDTO {

    private String reference;
    private Integer tableNumber;
    private LocalDateTime paidAt;
    private BigDecimal amount;
    private String currency;
    private List<OrderResponseDTO> orders;

    public ReceiptDTO() {
    }

    public ReceiptDTO(String reference, Integer tableNumber, LocalDateTime paidAt,
                      BigDecimal amount, String currency, List<OrderResponseDTO> orders) {
        this.reference = reference;
        this.tableNumber = tableNumber;
        this.paidAt = paidAt;
        this.amount = amount;
        this.currency = currency;
        this.orders = orders;
    }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<OrderResponseDTO> getOrders() { return orders; }
    public void setOrders(List<OrderResponseDTO> orders) { this.orders = orders; }
}
