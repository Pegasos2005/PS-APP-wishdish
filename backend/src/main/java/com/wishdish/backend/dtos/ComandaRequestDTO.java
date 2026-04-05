package com.wishdish.backend.dtos;

import java.util.List;

public class ComandaRequestDTO {

    private Long numeroMesa;
    private List<Long> productosIds;

    public ComandaRequestDTO() {
    }

    public Long getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Long numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public List<Long> getProductosIds() {
        return productosIds;
    }

    public void setProductosIds(List<Long> productosIds) {
        this.productosIds = productosIds;
    }
}
