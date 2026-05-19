package com.wishdish.dtos;

public class ConfirmPaymentRequestDTO {
    private String paymentIntentId;

    public ConfirmPaymentRequestDTO() {}

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
}
