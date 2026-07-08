package com.wishdish.dtos;
import java.math.BigDecimal;
import java.util.List;

public class RevenueReportDTO {
    private String range;
    private BigDecimal total;
    private List<ChartPointDTO> points;

    // Getters y Setters
    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<ChartPointDTO> getPoints() { return points; }
    public void setPoints(List<ChartPointDTO> points) { this.points = points; }
}
