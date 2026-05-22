package com.wishdish.dtos;

public class CreateIntentResponseDTO {
    private String clientSecret;
    private String paymentIntentId;
    private Long amountCents;
    private String currency;

    public CreateIntentResponseDTO() {}

    public CreateIntentResponseDTO(String clientSecret, String paymentIntentId, Long amountCents, String currency) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.amountCents = amountCents;
        this.currency = currency;
    }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public Long getAmountCents() { return amountCents; }
    public void setAmountCents(Long amountCents) { this.amountCents = amountCents; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
