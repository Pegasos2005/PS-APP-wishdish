package com.wishdish.backend.controllers;

import com.wishdish.backend.dtos.MenuCategoriaDTO;
import com.wishdish.backend.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuCategoriaDTO>> obtenerMenuCompleto() {
        List<MenuCategoriaDTO> menu = menuService.obtenerMenuCompleto();
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MenuCategoriaDTO>> obtenerMenuDisponible() {
        List<MenuCategoriaDTO> menu = menuService.obtenerMenuDisponible();
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> obtenerMenuPorCategoria(@PathVariable Long categoriaId) {
        try {
            MenuCategoriaDTO menuCategoria = menuService.obtenerMenuPorCategoria(categoriaId);
            return ResponseEntity.ok(menuCategoria);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/categoria/{categoriaId}/disponibles")
    public ResponseEntity<?> obtenerMenuDisponiblePorCategoria(@PathVariable Long categoriaId) {
        try {
            MenuCategoriaDTO menuCategoria = menuService.obtenerMenuDisponiblePorCategoria(categoriaId);
            return ResponseEntity.ok(menuCategoria);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}