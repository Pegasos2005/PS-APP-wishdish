package com.wishdish.backend.dto;

import com.wishdish.backend.entity.Producto;
import java.util.List;

/**
 * DTO (Data Transfer Object) para representar una categoría con sus productos.
 *
 * Se utiliza para enviar al frontend la estructura completa del menú:
 * - Información de la categoría
 * - Lista de productos que pertenecen a esa categoría
 *
 * Ejemplo JSON:
 * {
 *   "categoriaId": 1,
 *   "categoriaNombre": "Hamburguesas",
 *   "categoriaDescripcion": "Nuestras mejores hamburguesas",
 *   "productos": [
 *     {
 *       "id": 101,
 *       "nombre": "Hamburguesa Clásica",
 *       "descripcion": "...",
 *       "precio": 12.50,
 *       "imagen": "assets/hamburguesa.jpg",
 *       "disponible": true
 *     },
 *     ...
 *   ]
 * }
 */
public class MenuCategoriaDTO {

    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaDescripcion;
    private List<Producto> productos;

    // ===========================
    // CONSTRUCTORES
    // ===========================

    public MenuCategoriaDTO() {
    }

    public MenuCategoriaDTO(Long categoriaId, String categoriaNombre, String categoriaDescripcion, List<Producto> productos) {
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.categoriaDescripcion = categoriaDescripcion;
        this.productos = productos;
    }

    // ===========================
    // GETTERS Y SETTERS
    // ===========================

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getCategoriaDescripcion() {
        return categoriaDescripcion;
    }

    public void setCategoriaDescripcion(String categoriaDescripcion) {
        this.categoriaDescripcion = categoriaDescripcion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "MenuCategoriaDTO{" +
                "categoriaId=" + categoriaId +
                ", categoriaNombre='" + categoriaNombre + '\'' +
                ", categoriaDescripcion='" + categoriaDescripcion + '\'' +
                ", productos=" + (productos != null ? productos.size() + " productos" : "null") +
                '}';
    }
}
