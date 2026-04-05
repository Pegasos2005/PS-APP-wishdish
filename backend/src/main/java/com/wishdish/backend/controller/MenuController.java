package com.wishdish.backend.controller;

import com.wishdish.backend.dto.MenuCategoriaDTO;
import com.wishdish.backend.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar el Menú del restaurante.
 *
 * Este controller expone endpoints HTTP para que el frontend (Angular) pueda:
 * - Obtener el menú completo organizado por categorías
 * - Obtener solo productos disponibles
 * - Filtrar por categoría específica
 *
 * Todas las rutas empiezan con: http://localhost:8080/api/menu
 *
 * Historia de Usuario 1: El cliente ve los platos disponibles ordenados por categorías
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde Angular
public class MenuController {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    /**
     * Service que contiene la lógica de negocio del menú.
     * Spring lo inyecta automáticamente.
     */
    @Autowired
    private MenuService menuService;

    // ===========================
    // ENDPOINTS REST
    // ===========================

    /**
     * Endpoint para obtener el menú completo del restaurante.
     *
     * Devuelve todas las categorías con todos sus productos (disponibles y no disponibles).
     * Útil para el panel de administración donde se quiere ver todo el catálogo.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/menu
     *
     * Respuesta (200 OK):
     * [
     *   {
     *     "categoriaId": 1,
     *     "categoriaNombre": "Hamburguesas",
     *     "categoriaDescripcion": "Nuestras mejores hamburguesas",
     *     "productos": [
     *       {
     *         "id": 101,
     *         "nombre": "Hamburguesa Clásica",
     *         "descripcion": "Carne de res con queso",
     *         "precio": 12.50,
     *         "imagen": "assets/hamburguesa.jpg",
     *         "disponible": true,
     *         "categoria": { "id": 1, "nombre": "Hamburguesas", ... }
     *       },
     *       ...
     *     ]
     *   },
     *   {
     *     "categoriaId": 2,
     *     "categoriaNombre": "Postres",
     *     "categoriaDescripcion": "Dulces y postres caseros",
     *     "productos": [ ... ]
     *   }
     * ]
     *
     * @return Lista de categorías con todos sus productos
     */
    @GetMapping
    public ResponseEntity<List<MenuCategoriaDTO>> obtenerMenuCompleto() {
        List<MenuCategoriaDTO> menu = menuService.obtenerMenuCompleto();
        return ResponseEntity.ok(menu);
    }

    /**
     * Endpoint para obtener el menú con solo productos disponibles.
     *
     * Devuelve todas las categorías pero solo con productos marcados como disponibles.
     * Este es el endpoint que debe usar el frontend del cliente para mostrar el menú.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/menu/disponibles
     *
     * Respuesta (200 OK):
     * [
     *   {
     *     "categoriaId": 1,
     *     "categoriaNombre": "Hamburguesas",
     *     "categoriaDescripcion": "Nuestras mejores hamburguesas",
     *     "productos": [
     *       {
     *         "id": 101,
     *         "nombre": "Hamburguesa Clásica",
     *         "disponible": true,
     *         ...
     *       }
     *     ]
     *   }
     * ]
     *
     * NOTA: Solo incluye categorías que tienen al menos un producto disponible
     *
     * @return Lista de categorías con solo productos disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<MenuCategoriaDTO>> obtenerMenuDisponible() {
        List<MenuCategoriaDTO> menu = menuService.obtenerMenuDisponible();
        return ResponseEntity.ok(menu);
    }

    /**
     * Endpoint para obtener el menú de una categoría específica.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/menu/categoria/1
     *
     * Respuestas:
     * - 200 OK: MenuCategoriaDTO con la categoría y todos sus productos
     * - 404 NOT FOUND: La categoría no existe
     *
     * @param categoriaId ID de la categoría (se extrae de la URL)
     * @return MenuCategoriaDTO con la categoría y sus productos
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> obtenerMenuPorCategoria(@PathVariable Long categoriaId) {
        try {
            MenuCategoriaDTO menuCategoria = menuService.obtenerMenuPorCategoria(categoriaId);
            return ResponseEntity.ok(menuCategoria);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint para obtener el menú de una categoría con solo productos disponibles.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/menu/categoria/1/disponibles
     *
     * Respuestas:
     * - 200 OK: MenuCategoriaDTO con la categoría y sus productos disponibles
     * - 404 NOT FOUND: La categoría no existe
     *
     * @param categoriaId ID de la categoría (se extrae de la URL)
     * @return MenuCategoriaDTO con la categoría y sus productos disponibles
     */
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
