package com.wishdish.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "items_comanda")
public class ItemComanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItem estado = EstadoItem.en_cocina;

    @Column(name = "notas_item", columnDefinition = "TEXT")
    private String notasItem;

    public enum EstadoItem {
        en_cocina,
        preparado
    }

    public ItemComanda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public EstadoItem getEstado() {
        return estado;
    }

    public void setEstado(EstadoItem estado) {
        this.estado = estado;
    }

    public void avanzarEstado() {
        switch (this.estado) {
            case en_cocina:
                this.estado = EstadoItem.preparado;
                break;
            case preparado:
                break;
        }
    }

    public String getNotasItem() {
        return notasItem;
    }

    public void setNotasItem(String notasItem) {
        this.notasItem = notasItem;
    }

    @Override
    public String toString() {
        return "ItemComanda{" +
                "id=" + id +
                ", producto=" + (producto != null ? producto.getNombre() : "null") +
                ", cantidad=" + cantidad +
                ", estado=" + estado +
                '}';
    }
}
