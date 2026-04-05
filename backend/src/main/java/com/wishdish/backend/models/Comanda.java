package com.wishdish.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Column(name = "fecha_comanda", insertable = false, updatable = false)
    private LocalDateTime fechaComanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoComanda estado = EstadoComanda.en_cocina;

    @Column(name = "notas_generales", columnDefinition = "TEXT")
    private String notasGenerales;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ItemComanda> items = new ArrayList<>();

    public enum EstadoComanda {
        en_cocina,
        servida,
        pagada
    }

    public Comanda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalDateTime getFechaComanda() {
        return fechaComanda;
    }

    public EstadoComanda getEstado() {
        return estado;
    }

    public void setEstado(EstadoComanda estado) {
        this.estado = estado;
    }

    public void avanzarEstado() {
        switch (this.estado) {
            case en_cocina:
                this.estado = EstadoComanda.servida;
                break;
            case servida:
                this.estado = EstadoComanda.pagada;
                break;
            case pagada:
                break;
        }
    }

    public String getNotasGenerales() {
        return notasGenerales;
    }

    public void setNotasGenerales(String notasGenerales) {
        this.notasGenerales = notasGenerales;
    }

    public List<ItemComanda> getItems() {
        return items;
    }

    public void setItems(List<ItemComanda> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Comanda{" +
                "id=" + id +
                ", mesa=" + (mesa != null ? mesa.getNumeroMesa() : "null") +
                ", fechaComanda=" + fechaComanda +
                ", estado=" + estado +
                ", items=" + items.size() +
                '}';
    }
}
