package com.wishdish.dtos;

public class ManualItemRequestDTO {
    private Integer productId;
    private Integer quantity;
    private String observations; // Opcional, por si el admin quiere añadir "Sin hielo", etc.

    public ManualItemRequestDTO() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
