package com.wishdish.dtos;
import java.math.BigDecimal;
import java.util.List;
import com.wishdish.dtos.OrderResponseDTO;

public class DailyReportDTO {
    private BigDecimal totalSales;
    private Integer totalTransactions;
    private BigDecimal averageOrder;
    private List<HourlySalesDTO> hourlyData;
    private List<OrderResponseDTO> orders;

    // Getters y Setters
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }
    public BigDecimal getAverageOrder() { return averageOrder; }
    public void setAverageOrder(BigDecimal averageOrder) { this.averageOrder = averageOrder; }
    public List<HourlySalesDTO> getHourlyData() { return hourlyData; }
    public void setHourlyData(List<HourlySalesDTO> hourlyData) { this.hourlyData = hourlyData; }

    public List<OrderResponseDTO> getOrders() { return orders; }
    public void setOrders(List<OrderResponseDTO> orders) { this.orders = orders; }
}