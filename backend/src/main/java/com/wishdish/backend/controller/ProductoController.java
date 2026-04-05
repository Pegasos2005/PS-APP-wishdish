package com.wishdish.backend.controller;

import com.wishdish.backend.entity.Producto;
import com.wishdish.backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar los Productos.
 *
 * Este controller expone endpoints HTTP para que el frontend (Angular) pueda:
 * - Crear productos
 * - Listar todos los productos
 * - Buscar productos por ID, nombre o categoría
 * - Actualizar productos
 * - Cambiar disponibilidad de productos
 * - Eliminar productos
 *
 * Todas las rutas empiezan con: http://localhost:8080/api/productos
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde Angular
public class ProductoController {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    /**
     * Service que contiene la lógica de negocio.
     * Spring lo inyecta automáticamente.
     */
    @Autowired
    private ProductoService productoService;

    // ===========================
    // ENDPOINTS REST
    // ===========================

    /**
     * Endpoint para crear un nuevo producto.
     *
     * Petición HTTP:
     * POST http://localhost:8080/api/productos
     * Content-Type: application/json
     *
     * Body (JSON):
     * {
     *   "nombre": "Hamburguesa Clásica",
     *   "descripcion": "Carne de res, queso, lechuga, tomate",
     *   "precio": 12.50,
     *   "imagen": "assets/hamburguesa.jpg",
     *   "disponible": true,
     *   "categoria": {
     *     "id": 1
     *   }
     * }
     *
     * Respuestas:
     * - 201 CREATED: Producto creado exitosamente
     * - 400 BAD REQUEST: Error de validación
     *
     * @param producto Objeto Producto enviado en el body de la petición
     * @return ResponseEntity con el producto creado y código HTTP 201
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint para obtener todos los productos.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos
     *
     * Respuesta:
     * - 200 OK: Lista de productos en formato JSON
     *
     * @return Lista de todos los productos
     */
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para obtener solo los productos disponibles.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/disponibles
     *
     * Respuesta:
     * - 200 OK: Lista de productos con disponible = true
     *
     * @return Lista de productos disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Producto>> obtenerProductosDisponibles() {
        List<Producto> productos = productoService.obtenerProductosDisponibles();
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para obtener un producto por su ID.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/1
     *
     * Respuestas:
     * - 200 OK: Producto encontrado
     * - 404 NOT FOUND: No existe un producto con ese ID
     *
     * @param id ID del producto (se extrae de la URL)
     * @return ResponseEntity con el producto o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);

        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró un producto con el ID: " + id);
        }
    }

    /**
     * Endpoint para buscar un producto por su nombre.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/buscar?nombre=Hamburguesa
     *
     * Respuestas:
     * - 200 OK: Lista de productos que contienen el nombre
     *
     * @param nombre Texto a buscar en el nombre (parámetro de query)
     * @return Lista de productos que coinciden
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductosPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para obtener todos los productos de una categoría.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/categoria/1
     *
     * Respuesta:
     * - 200 OK: Lista de productos de la categoría
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos de la categoría
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Long categoriaId) {
        List<Producto> productos = productoService.obtenerProductosPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para obtener productos disponibles de una categoría.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/categoria/1/disponibles
     *
     * Respuesta:
     * - 200 OK: Lista de productos disponibles de la categoría
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos disponibles de la categoría
     */
    @GetMapping("/categoria/{categoriaId}/disponibles")
    public ResponseEntity<List<Producto>> obtenerProductosDisponiblesPorCategoria(@PathVariable Long categoriaId) {
        List<Producto> productos = productoService.obtenerProductosDisponiblesPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * Endpoint para actualizar un producto existente.
     *
     * Petición HTTP:
     * PUT http://localhost:8080/api/productos/1
     * Content-Type: application/json
     *
     * Body (JSON):
     * {
     *   "nombre": "Hamburguesa Premium",
     *   "descripcion": "Carne angus, queso gruyere, cebolla caramelizada",
     *   "precio": 15.90,
     *   "imagen": "assets/hamburguesa-premium.jpg",
     *   "disponible": true,
     *   "categoria": {
     *     "id": 1
     *   }
     * }
     *
     * Respuestas:
     * - 200 OK: Producto actualizado exitosamente
     * - 400 BAD REQUEST: Error de validación
     * - 404 NOT FOUND: El producto no existe
     *
     * @param id ID del producto a actualizar (de la URL)
     * @param producto Objeto con los nuevos datos (del body)
     * @return ResponseEntity con el producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("No existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    /**
     * Endpoint para cambiar la disponibilidad de un producto.
     *
     * Petición HTTP:
     * PATCH http://localhost:8080/api/productos/1/disponibilidad?disponible=false
     *
     * Respuestas:
     * - 200 OK: Disponibilidad actualizada
     * - 404 NOT FOUND: El producto no existe
     *
     * @param id ID del producto
     * @param disponible true para marcar como disponible, false para no disponible
     * @return ResponseEntity con el producto actualizado
     */
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<?> cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean disponible) {
        try {
            Producto productoActualizado = productoService.cambiarDisponibilidad(id, disponible);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint para eliminar un producto.
     *
     * Petición HTTP:
     * DELETE http://localhost:8080/api/productos/1
     *
     * Respuestas:
     * - 204 NO CONTENT: Producto eliminado exitosamente
     * - 404 NOT FOUND: El producto no existe
     *
     * @param id ID del producto a eliminar (de la URL)
     * @return ResponseEntity con código HTTP apropiado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint para contar el total de productos.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/contar
     *
     * Respuesta:
     * - 200 OK: Número de productos
     *
     * @return Número total de productos
     */
    @GetMapping("/contar")
    public ResponseEntity<Long> contarProductos() {
        long total = productoService.contarProductos();
        return ResponseEntity.ok(total);
    }

    /**
     * Endpoint para contar productos de una categoría.
     *
     * Petición HTTP:
     * GET http://localhost:8080/api/productos/contar/categoria/1
     *
     * Respuesta:
     * - 200 OK: Número de productos en la categoría
     *
     * @param categoriaId ID de la categoría
     * @return Número de productos en la categoría
     */
    @GetMapping("/contar/categoria/{categoriaId}")
    public ResponseEntity<Long> contarProductosPorCategoria(@PathVariable Long categoriaId) {
        long total = productoService.contarProductosPorCategoria(categoriaId);
        return ResponseEntity.ok(total);
    }
}
