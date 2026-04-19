package com.wishdish.dtos;

import java.util.List;

public class OrderItemRequestDTO {
    private Integer productId;
    private Integer quantity;
    private List<String> addedExtras;
    private List<String> removedDefaults;

    // Getters y Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public List<String> getAddedExtras() { return addedExtras; }
    public void setAddedExtras(List<String> addedExtras) { this.addedExtras = addedExtras; }

    public List<String> getRemovedDefaults() { return removedDefaults; }
    public void setRemovedDefaults(List<String> removedDefaults) { this.removedDefaults = removedDefaults; }
}
