package com.wishdish.backend.dtos;

import com.wishdish.backend.models.Producto;
import java.util.List;

public class MenuCategoriaDTO {

    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaDescripcion;
    private List<Producto> productos;

    public MenuCategoriaDTO() {
    }

    public MenuCategoriaDTO(Long categoriaId, String categoriaNombre, String categoriaDescripcion, List<Producto> productos) {
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.categoriaDescripcion = categoriaDescripcion;
        this.productos = productos;
    }

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
