package com.wishdish.backend.controller;

import com.wishdish.backend.entity.Categoria;
import com.wishdish.backend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar las Categorías.
 *
 * Este controller expone endpoints HTTP para que el frontend (Angular) pueda:
 * - Crear categorías
 * - Listar todas las categorías
 * - Buscar categorías por ID
 * - Actualizar categorías
 * - Eliminar categorías
 *
 * Todas las rutas empiezan con: http://localhost:8080/api/categorias
 */
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde Angular
public class CategoriaController {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    /**
     * Service que contiene la lógica de negocio.
     * Spring lo inyecta automáticamente.
     */
    @Autowired
    private CategoriaService categoriaService;

    // ===========================
    // ENDPOINTS REST
    // ===========================

    /**
     * Endpoint para crear una nueva categoría.
     *
     * Petición HTTP:
     * POST http://localhost:8080/api/categorias
     * Content-Type: application/json
     *
     * Body (JSON):
     * {
     *   "nombre": "Postres",
     *   "descripcion": "Dulces y postres caseros"
     * }
     *
     * Respuestas:
     * - 201 CREATED: Categoría creada exitosamente (devuelve la categoría con ID y fechas)
     * - 400 BAD REQUEST: Error de validación (nombre vacío o duplicado)
     *
     * @param categoria Objeto Categoria enviado en el body de la petición
     * @return ResponseEntity con la categoría creada y código HTTP 201
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        try {
            Categoria nuevaCategoria = categoriaService.crearCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCategoria);
        } catch (IllegalArgumentException e) {
            // Si hay un error de validación, devolvemos 400 con el mensaje de error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint para obtener todas las categorías.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/categorias
     *
     * Respuesta:
     * - 200 OK: Lista de categorías en formato JSON (puede estar vacía [])
     *
     * Ejemplo de respuesta:
     * [
     *   {
     *     "id": 1,
     *     "nombre": "Entrantes",
     *     "descripcion": "Platos para comenzar",
     *     "fechaCreacion": "2024-01-15T10:30:00",
     *     "fechaActualizacion": "2024-01-15T10:30:00"
     *   },
     *   {
     *     "id": 2,
     *     "nombre": "Postres",
     *     "descripcion": "Dulces y postres",
     *     "fechaCreacion": "2024-01-16T14:20:00",
     *     "fechaActualizacion": "2024-01-16T14:20:00"
     *   }
     * ]
     *
     * @return Lista de todas las categorías
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodasLasCategorias() {
        List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Endpoint para obtener una categoría por su ID.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/categorias/1
     *
     * Respuestas:
     * - 200 OK: Categoría encontrada (devuelve el objeto JSON)
     * - 404 NOT FOUND: No existe una categoría con ese ID
     *
     * @param id ID de la categoría (se extrae de la URL)
     * @return ResponseEntity con la categoría o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.obtenerCategoriaPorId(id);

        if (categoria.isPresent()) {
            return ResponseEntity.ok(categoria.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró una categoría con el ID: " + id);
        }
    }

    /**
     * Endpoint para buscar una categoría por su nombre.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/categorias/buscar?nombre=Postres
     *
     * Respuestas:
     * - 200 OK: Categoría encontrada
     * - 404 NOT FOUND: No existe una categoría con ese nombre
     *
     * @param nombre Nombre de la categoría (parámetro de query)
     * @return ResponseEntity con la categoría o 404 si no existe
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> obtenerCategoriaPorNombre(@RequestParam String nombre) {
        Optional<Categoria> categoria = categoriaService.obtenerCategoriaPorNombre(nombre);

        if (categoria.isPresent()) {
            return ResponseEntity.ok(categoria.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró una categoría con el nombre: " + nombre);
        }
    }

    /**
     * Endpoint para actualizar una categoría existente.
     *
     * Petición HTTP:
     * PUT http://localhost:8080/api/categorias/1
     * Content-Type: application/json
     *
     * Body (JSON):
     * {
     *   "nombre": "Postres Caseros",
     *   "descripcion": "Postres hechos en casa"
     * }
     *
     * Respuestas:
     * - 200 OK: Categoría actualizada exitosamente
     * - 400 BAD REQUEST: Error de validación
     * - 404 NOT FOUND: La categoría no existe
     *
     * @param id ID de la categoría a actualizar (de la URL)
     * @param categoria Objeto con los nuevos datos (del body)
     * @return ResponseEntity con la categoría actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {
        try {
            Categoria categoriaActualizada = categoriaService.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (IllegalArgumentException e) {
            // Puede ser 404 (no existe) o 400 (nombre duplicado)
            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    /**
     * Endpoint para eliminar una categoría.
     *
     * Petición HTTP:
     * DELETE http://localhost:8080/api/categorias/1
     *
     * Respuestas:
     * - 204 NO CONTENT: Categoría eliminada exitosamente (sin body en la respuesta)
     * - 404 NOT FOUND: La categoría no existe
     *
     * @param id ID de la categoría a eliminar (de la URL)
     * @return ResponseEntity con código HTTP apropiado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint para contar el total de categorías.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/categorias/contar
     *
     * Respuesta:
     * - 200 OK: Número de categorías (ej: 5)
     *
     * @return Número total de categorías
     */
    @GetMapping("/contar")
    public ResponseEntity<Long> contarCategorias() {
        long total = categoriaService.contarCategorias();
        return ResponseEntity.ok(total);
    }
}
