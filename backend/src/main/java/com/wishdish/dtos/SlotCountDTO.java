package com.wishdish.dtos;

public class SlotCountDTO {
    private String slot;
    private Integer count;

    public SlotCountDTO(String slot, Integer count) {
        this.slot = slot;
        this.count = count;
    }
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
