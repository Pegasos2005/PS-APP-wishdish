package com.wishdish.backend.service;

import com.wishdish.backend.entity.Producto;
import com.wishdish.backend.entity.Categoria;
import com.wishdish.backend.repository.ProductoRepository;
import com.wishdish.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para los Productos.
 *
 * Esta capa intermedia entre el Controller y el Repository se encarga de:
 * - Validaciones de negocio
 * - Transformaciones de datos
 * - Manejo de excepciones
 * - Lógica compleja que no debe estar en el Controller
 *
 * Regla de oro: Los Controllers son "tontos" (solo reciben/envían datos),
 * los Services son "inteligentes" (contienen la lógica).
 */
@Service
public class ProductoService {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    /**
     * Repository para acceder a la base de datos de productos.
     * @Autowired: Spring inyecta automáticamente una instancia del Repository.
     */
    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Repository para validar que las categorías existen.
     */
    @Autowired
    private CategoriaRepository categoriaRepository;

    // ===========================
    // MÉTODOS CRUD
    // ===========================

    /**
     * Crea un nuevo producto en la base de datos.
     *
     * Validaciones:
     * - El nombre no puede estar vacío
     * - El precio debe ser mayor a cero
     * - La categoría debe existir
     * - No puede existir otro producto con el mismo nombre
     *
     * @param producto Objeto Producto con los datos a guardar
     * @return El producto guardado (con ID generado y fechas establecidas)
     * @throws IllegalArgumentException si alguna validación falla
     */
    public Producto crearProducto(Producto producto) {
        // Validación 1: El nombre no puede ser nulo o vacío
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        // Validación 2: El precio debe ser mayor a cero
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        // Validación 3: La categoría debe existir
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría asignada");
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findById(producto.getCategoria().getId());
        if (categoriaExistente.isEmpty()) {
            throw new IllegalArgumentException("La categoría con ID " + producto.getCategoria().getId() + " no existe");
        }

        // Validación 4: No puede existir otro producto con el mismo nombre
        Optional<Producto> productoExistente = productoRepository.findByNombreIgnoreCase(producto.getNombre());
        if (productoExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + producto.getNombre());
        }

        // Establecer la categoría completa (con todos sus datos)
        producto.setCategoria(categoriaExistente.get());

        // Si pasa las validaciones, guardamos en la base de datos
        return productoRepository.save(producto);
    }

    /**
     * Obtiene todos los productos de la base de datos.
     *
     * @return Lista con todos los productos (vacía si no hay ninguno)
     */
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    /**
     * Obtiene solo los productos disponibles.
     *
     * @return Lista de productos con disponible = true
     */
    public List<Producto> obtenerProductosDisponibles() {
        return productoRepository.findByDisponibleTrue();
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id Identificador del producto
     * @return Optional con el producto si existe, Optional.empty() si no existe
     */
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Busca un producto por su nombre exacto.
     *
     * @param nombre Nombre del producto
     * @return Optional con el producto si existe, Optional.empty() si no existe
     */
    public Optional<Producto> obtenerProductoPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    /**
     * Obtiene todos los productos de una categoría específica.
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos de la categoría (vacía si no hay productos)
     */
    public List<Producto> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    /**
     * Obtiene los productos disponibles de una categoría específica.
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos disponibles de la categoría
     */
    public List<Producto> obtenerProductosDisponiblesPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndDisponibleTrue(categoriaId);
    }

    /**
     * Busca productos cuyo nombre contenga el texto especificado.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden
     */
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre);
    }

    /**
     * Actualiza un producto existente.
     *
     * Validaciones:
     * - El producto debe existir
     * - Si se cambia el nombre, no debe existir otro producto con ese nombre
     * - Si se cambia la categoría, esta debe existir
     * - El precio debe ser mayor a cero
     *
     * @param id ID del producto a actualizar
     * @param productoActualizado Objeto con los nuevos datos
     * @return El producto actualizado
     * @throws IllegalArgumentException si alguna validación falla
     */
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        // Validación 1: El producto debe existir
        Optional<Producto> productoExistente = productoRepository.findById(id);
        if (productoExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe un producto con el ID: " + id);
        }

        Producto producto = productoExistente.get();

        // Validación 2: El nombre no puede estar vacío
        if (productoActualizado.getNombre() == null || productoActualizado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        // Validación 3: Si se cambió el nombre, verificar que no esté duplicado
        if (!producto.getNombre().equalsIgnoreCase(productoActualizado.getNombre())) {
            Optional<Producto> otroProductoConMismoNombre = productoRepository.findByNombreIgnoreCase(productoActualizado.getNombre());
            if (otroProductoConMismoNombre.isPresent()) {
                throw new IllegalArgumentException("Ya existe un producto con el nombre: " + productoActualizado.getNombre());
            }
        }

        // Validación 4: El precio debe ser mayor a cero
        if (productoActualizado.getPrecio() == null || productoActualizado.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        // Validación 5: Si se cambió la categoría, verificar que exista
        if (productoActualizado.getCategoria() != null && productoActualizado.getCategoria().getId() != null) {
            Optional<Categoria> categoriaExistente = categoriaRepository.findById(productoActualizado.getCategoria().getId());
            if (categoriaExistente.isEmpty()) {
                throw new IllegalArgumentException("La categoría con ID " + productoActualizado.getCategoria().getId() + " no existe");
            }
            producto.setCategoria(categoriaExistente.get());
        }

        // Actualizar los campos
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setImagen(productoActualizado.getImagen());
        producto.setDisponible(productoActualizado.getDisponible());

        // Guardar cambios (el @PreUpdate actualizará automáticamente fechaActualizacion)
        return productoRepository.save(producto);
    }

    /**
     * Marca un producto como disponible o no disponible.
     *
     * @param id ID del producto
     * @param disponible true para marcar como disponible, false para no disponible
     * @return El producto actualizado
     * @throws IllegalArgumentException si el producto no existe
     */
    public Producto cambiarDisponibilidad(Long id, Boolean disponible) {
        Optional<Producto> productoExistente = productoRepository.findById(id);
        if (productoExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe un producto con el ID: " + id);
        }

        Producto producto = productoExistente.get();
        producto.setDisponible(disponible);
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id ID del producto a eliminar
     * @throws IllegalArgumentException si el producto no existe
     */
    public void eliminarProducto(Long id) {
        // Validación: El producto debe existir
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe un producto con el ID: " + id);
        }

        productoRepository.deleteById(id);
    }

    /**
     * Cuenta el número total de productos en la base de datos.
     *
     * @return Número de productos
     */
    public long contarProductos() {
        return productoRepository.count();
    }

    /**
     * Cuenta cuántos productos hay en una categoría específica.
     *
     * @param categoriaId ID de la categoría
     * @return Número de productos en la categoría
     */
    public long contarProductosPorCategoria(Long categoriaId) {
        return productoRepository.countByCategoriaId(categoriaId);
    }
}
