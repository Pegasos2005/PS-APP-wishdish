package com.wishdish.dtos;

import com.wishdish.models.OrderItem;

import java.math.BigDecimal;

public class OrderItemDTO {
    private Integer id;
    private String productName; // Extraído de product.getName()
    private String status;
    private String itemNotes;
    private Integer quantity;
    private BigDecimal productPrice;

    public OrderItemDTO() {}

    public OrderItemDTO(OrderItem item) {
        this.id = item.getId();
        this.productName = item.getProduct() != null ? item.getProduct().getName() : "Plato desconocido";
        this.status = item.getStatus().name();
        this.itemNotes = item.getItemNotes();
        this.quantity = item.getQuantity();
        if (item.getProduct() != null) {
            this.productName = item.getProduct().getName();
            this.productPrice = item.getProduct().getPrice();
        }
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getItemNotes() { return itemNotes; }
    public void setItemNotes(String itemNotes) { this.itemNotes = itemNotes; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}