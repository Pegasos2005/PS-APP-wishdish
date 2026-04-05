package com.wishdish.backend.dtos;

import com.wishdish.backend.models.Comanda;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ComandaResponseDTO {

    private Long id;
    private Integer numeroMesa;
    private LocalDateTime fechaComanda;
    private String estado;
    private String notasGenerales;
    private List<ItemComandaDTO> items;

    public ComandaResponseDTO() {
    }

    public ComandaResponseDTO(Comanda comanda) {
        this.id = comanda.getId();
        this.numeroMesa = comanda.getMesa() != null ? comanda.getMesa().getNumeroMesa() : null;
        this.fechaComanda = comanda.getFechaComanda();
        this.estado = comanda.getEstado().toString();
        this.notasGenerales = comanda.getNotasGenerales();
        this.items = comanda.getItems().stream()
                .map(ItemComandaDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public LocalDateTime getFechaComanda() {
        return fechaComanda;
    }

    public void setFechaComanda(LocalDateTime fechaComanda) {
        this.fechaComanda = fechaComanda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotasGenerales() {
        return notasGenerales;
    }

    public void setNotasGenerales(String notasGenerales) {
        this.notasGenerales = notasGenerales;
    }

    public List<ItemComandaDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemComandaDTO> items) {
        this.items = items;
    }
}
