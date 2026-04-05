package com.wishdish.backend.services;

import com.wishdish.backend.dtos.MenuCategoriaDTO;
import com.wishdish.backend.models.Categoria;
import com.wishdish.backend.models.Producto;
import com.wishdish.backend.repositories.CategoriaRepository;
import com.wishdish.backend.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<MenuCategoriaDTO> obtenerMenuCompleto() {
        List<Categoria> categorias = categoriaRepository.findAll();

        return categorias.stream()
                .map(categoria -> {
                    List<Producto> productos = productoRepository.findByCategoriaId(categoria.getId());

                    return new MenuCategoriaDTO(
                            categoria.getId(),
                            categoria.getNombre(),
                            categoria.getDescripcion(),
                            productos
                    );
                })
                .collect(Collectors.toList());
    }

    public List<MenuCategoriaDTO> obtenerMenuDisponible() {
        List<Categoria> categorias = categoriaRepository.findAll();

        return categorias.stream()
                .map(categoria -> {
                    List<Producto> productosDisponibles = productoRepository
                            .findByCategoriaIdAndDisponibleTrue(categoria.getId());

                    return new MenuCategoriaDTO(
                            categoria.getId(),
                            categoria.getNombre(),
                            categoria.getDescripcion(),
                            productosDisponibles
                    );
                })
                .filter(menuCategoria -> !menuCategoria.getProductos().isEmpty())
                .collect(Collectors.toList());
    }

    public MenuCategoriaDTO obtenerMenuPorCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una categoría con el ID: " + categoriaId));

        List<Producto> productos = productoRepository.findByCategoriaId(categoriaId);

        return new MenuCategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                productos
        );
    }

    public MenuCategoriaDTO obtenerMenuDisponiblePorCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una categoría con el ID: " + categoriaId));

        List<Producto> productosDisponibles = productoRepository
                .findByCategoriaIdAndDisponibleTrue(categoriaId);

        return new MenuCategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                productosDisponibles
        );
    }
}