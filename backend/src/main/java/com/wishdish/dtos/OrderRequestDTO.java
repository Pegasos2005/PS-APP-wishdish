package com.wishdish.dtos;

import java.util.List;

public class OrderRequestDTO {
    private Integer tableId;
    private List<Integer> productIds;

    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }

    public List<Integer> getProductIds() { return productIds; }
    public void setProductIds(List<Integer> productIds) { this.productIds = productIds; }
}