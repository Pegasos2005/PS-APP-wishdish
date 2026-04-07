package com.wishdish.dtos;

import com.wishdish.models.OrderItem;

public class OrderItemDTO {

    private Integer id;
    private ProductDTO product; // Usamos o ProductDTO e não a Entidade!
    private Integer quantity;
    private String status;
    private String itemNotes;

    public OrderItemDTO() {
    }

    public OrderItemDTO(OrderItem item) {
        this.id = item.getId();
        // Convertemos o produto real num DTO para o Frontend
        this.product = new ProductDTO(item.getProduct());
        this.quantity = item.getQuantity();
        this.status = item.getStatus() != null ? item.getStatus().name() : null;
        this.itemNotes = item.getItemNotes();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getItemNotes() { return itemNotes; }
    public void setItemNotes(String itemNotes) { this.itemNotes = itemNotes; }
}