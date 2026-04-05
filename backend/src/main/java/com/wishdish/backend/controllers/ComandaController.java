package com.wishdish.backend.controllers;

import com.wishdish.backend.dtos.ComandaRequestDTO;
import com.wishdish.backend.dtos.ComandaResponseDTO;
import com.wishdish.backend.models.Comanda;
import com.wishdish.backend.models.ItemComanda;
import com.wishdish.backend.services.ComandaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comandas")
@CrossOrigin(origins = "*")
public class ComandaController {

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping("/activas")
    public List<ComandaResponseDTO> getComandasActivas() {
        return comandaService.getComandasActivas();
    }

    @PostMapping
    public Comanda crearComanda(@RequestBody ComandaRequestDTO request) {
        return comandaService.crearComanda(request.getNumeroMesa(), request.getProductosIds());
    }

    @PutMapping("/items/{itemId}/avanzar")
    public ItemComanda avanzarEstadoItem(@PathVariable Long itemId) {
        return comandaService.avanzarEstadoItem(itemId);
    }
}
