package com.wishdish.dtos;

import java.util.List;

public class OrderRequestDTO {
    private Integer tableId;
    private List<OrderItemRequestDTO> items;
    private String generalNotes;

    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }

    public String getGeneralNotes() { return generalNotes; }
    public void setGeneralNotes(String generalNotes) { this.generalNotes = generalNotes; }
}