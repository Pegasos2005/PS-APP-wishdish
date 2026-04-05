package com.wishdish.backend.service;

import com.wishdish.backend.dto.MenuCategoriaDTO;
import com.wishdish.backend.entity.Categoria;
import com.wishdish.backend.entity.Producto;
import com.wishdish.backend.repository.CategoriaRepository;
import com.wishdish.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar el menú completo del restaurante.
 *
 * Este servicio se encarga de:
 * - Obtener todas las categorías con sus productos
 * - Filtrar productos disponibles
 * - Estructurar los datos en formato DTO para el frontend
 *
 * El frontend Angular consume estos datos para mostrar el menú organizado por categorías.
 */
@Service
public class MenuService {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // ===========================
    // MÉTODOS DE NEGOCIO
    // ===========================

    /**
     * Obtiene el menú completo: todas las categorías con sus productos.
     *
     * Estructura de retorno:
     * [
     *   {
     *     categoriaId: 1,
     *     categoriaNombre: "Hamburguesas",
     *     categoriaDescripcion: "...",
     *     productos: [ {...}, {...}, ... ]
     *   },
     *   {
     *     categoriaId: 2,
     *     categoriaNombre: "Postres",
     *     categoriaDescripcion: "...",
     *     productos: [ {...}, {...} ]
     *   },
     *   ...
     * ]
     *
     * @return Lista de MenuCategoriaDTO con categorías y productos
     */
    public List<MenuCategoriaDTO> obtenerMenuCompleto() {
        // 1. Obtener todas las categorías de la BD
        List<Categoria> categorias = categoriaRepository.findAll();

        // 2. Para cada categoría, obtener sus productos y crear un DTO
        return categorias.stream()
                .map(categoria -> {
                    // Obtener todos los productos de esta categoría
                    List<Producto> productos = productoRepository.findByCategoriaId(categoria.getId());

                    // Crear el DTO con la categoría y sus productos
                    return new MenuCategoriaDTO(
                            categoria.getId(),
                            categoria.getNombre(),
                            categoria.getDescripcion(),
                            productos
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el menú solo con productos disponibles.
     *
     * Útil para mostrar al cliente final solo los productos que puede ordenar.
     *
     * @return Lista de MenuCategoriaDTO con solo productos disponibles
     */
    public List<MenuCategoriaDTO> obtenerMenuDisponible() {
        // 1. Obtener todas las categorías de la BD
        List<Categoria> categorias = categoriaRepository.findAll();

        // 2. Para cada categoría, obtener solo sus productos disponibles
        return categorias.stream()
                .map(categoria -> {
                    // Obtener solo productos disponibles de esta categoría
                    List<Producto> productosDisponibles = productoRepository
                            .findByCategoriaIdAndDisponibleTrue(categoria.getId());

                    // Crear el DTO con la categoría y sus productos disponibles
                    return new MenuCategoriaDTO(
                            categoria.getId(),
                            categoria.getNombre(),
                            categoria.getDescripcion(),
                            productosDisponibles
                    );
                })
                // Filtrar categorías que no tienen productos disponibles (opcional)
                .filter(menuCategoria -> !menuCategoria.getProductos().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el menú de una categoría específica.
     *
     * @param categoriaId ID de la categoría
     * @return MenuCategoriaDTO con la categoría y sus productos
     * @throws IllegalArgumentException si la categoría no existe
     */
    public MenuCategoriaDTO obtenerMenuPorCategoria(Long categoriaId) {
        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una categoría con el ID: " + categoriaId));

        // Obtener los productos de la categoría
        List<Producto> productos = productoRepository.findByCategoriaId(categoriaId);

        // Crear y retornar el DTO
        return new MenuCategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                productos
        );
    }

    /**
     * Obtiene el menú de una categoría con solo productos disponibles.
     *
     * @param categoriaId ID de la categoría
     * @return MenuCategoriaDTO con la categoría y sus productos disponibles
     * @throws IllegalArgumentException si la categoría no existe
     */
    public MenuCategoriaDTO obtenerMenuDisponiblePorCategoria(Long categoriaId) {
        // Validar que la categoría existe
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una categoría con el ID: " + categoriaId));

        // Obtener solo productos disponibles de la categoría
        List<Producto> productosDisponibles = productoRepository
                .findByCategoriaIdAndDisponibleTrue(categoriaId);

        // Crear y retornar el DTO
        return new MenuCategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                productosDisponibles
        );
    }
}
