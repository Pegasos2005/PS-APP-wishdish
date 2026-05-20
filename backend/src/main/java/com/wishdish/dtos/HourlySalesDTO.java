package com.wishdish.dtos;
import java.math.BigDecimal;

public class HourlySalesDTO {
    private String hour;
    private BigDecimal amount;

    public HourlySalesDTO(String hour, BigDecimal amount) {
        this.hour = hour;
        this.amount = amount;
    }
    public String getHour() { return hour; }
    public void setHour(String hour) { this.hour = hour; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}