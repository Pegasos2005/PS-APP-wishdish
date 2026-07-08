package com.wishdish.dtos;
import java.util.List;

public class SlotDistributionReportDTO {
    private String range;
    private Integer totalOrders;
    private List<SlotCountDTO> slots;

    // Getters y Setters
    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public List<SlotCountDTO> getSlots() { return slots; }
    public void setSlots(List<SlotCountDTO> slots) { this.slots = slots; }
}
