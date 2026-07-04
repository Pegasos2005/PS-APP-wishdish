package com.wishdish.dtos;
import java.math.BigDecimal;

public class TopProductDTO {
    private String name;
    private Integer units;
    private BigDecimal revenue;

    public TopProductDTO(String name, Integer units, BigDecimal revenue) {
        this.name = name;
        this.units = units;
        this.revenue = revenue;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
