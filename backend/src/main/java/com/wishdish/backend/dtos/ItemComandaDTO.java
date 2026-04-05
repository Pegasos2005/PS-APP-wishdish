package com.wishdish.backend.dtos;

import com.wishdish.backend.models.ItemComanda;

public class ItemComandaDTO {

    private Long id;
    private ProductoDTO producto;
    private Integer cantidad;
    private String estado;
    private String notasItem;

    public ItemComandaDTO() {
    }

    public ItemComandaDTO(ItemComanda item) {
        this.id = item.getId();
        this.producto = new ProductoDTO(item.getProducto());
        this.cantidad = item.getCantidad();
        this.estado = item.getEstado().toString();
        this.notasItem = item.getNotasItem();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotasItem() {
        return notasItem;
    }

    public void setNotasItem(String notasItem) {
        this.notasItem = notasItem;
    }
}
