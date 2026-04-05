package com.wishdish.backend.repository;

import com.wishdish.backend.entity.Producto;
import com.wishdish.backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad Producto.
 *
 * Esta interfaz NO necesita implementación. Spring Data JPA genera automáticamente
 * todos los métodos CRUD (Create, Read, Update, Delete) en tiempo de ejecución.
 *
 * Al extender JpaRepository, heredamos métodos como:
 * - save(producto)           -> INSERT o UPDATE
 * - findById(id)             -> SELECT por ID
 * - findAll()                -> SELECT todos los productos
 * - deleteById(id)           -> DELETE por ID
 * - existsById(id)           -> Verificar si existe
 * - count()                  -> Contar registros
 * - etc.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su nombre exacto.
     *
     * Consulta generada automáticamente:
     * SELECT * FROM productos WHERE nombre = ?
     *
     * @param nombre Nombre del producto a buscar
     * @return Optional con el producto si existe, Optional.empty() si no existe
     */
    Optional<Producto> findByNombre(String nombre);

    /**
     * Busca todos los productos de una categoría específica.
     *
     * Consulta generada automáticamente:
     * SELECT * FROM productos WHERE categoria_id = ?
     *
     * @param categoria Categoría a la que pertenecen los productos
     * @return Lista de productos de la categoría (vacía si no hay productos)
     */
    List<Producto> findByCategoria(Categoria categoria);

    /**
     * Busca todos los productos de una categoría específica por ID.
     *
     * Consulta generada automáticamente:
     * SELECT * FROM productos WHERE categoria_id = ?
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos de la categoría (vacía si no hay productos)
     */
    List<Producto> findByCategoriaId(Long categoriaId);

    /**
     * Busca todos los productos disponibles (disponible = true).
     *
     * Consulta generada automáticamente:
     * SELECT * FROM productos WHERE disponible = true
     *
     * @return Lista de productos disponibles
     */
    List<Producto> findByDisponibleTrue();

    /**
     * Busca productos disponibles de una categoría específica.
     *
     * Consulta generada automáticamente:
     * SELECT * FROM productos WHERE categoria_id = ? AND disponible = true
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos disponibles de la categoría
     */
    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    /**
     * Busca un producto por nombre ignorando mayúsculas/minúsculas.
     *
     * Consulta generada:
     * SELECT * FROM productos WHERE LOWER(nombre) = LOWER(?)
     *
     * @param nombre Nombre del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findByNombreIgnoreCase(String nombre);

    /**
     * Busca productos cuyo nombre contenga el texto especificado.
     *
     * Consulta generada:
     * SELECT * FROM productos WHERE nombre LIKE %?%
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden
     */
    List<Producto> findByNombreContaining(String nombre);

    /**
     * Verifica si existe un producto con el nombre especificado.
     *
     * Consulta generada:
     * SELECT COUNT(*) > 0 FROM productos WHERE nombre = ?
     *
     * @param nombre Nombre del producto
     * @return true si existe, false si no existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Cuenta cuántos productos hay en una categoría específica.
     *
     * Consulta generada:
     * SELECT COUNT(*) FROM productos WHERE categoria_id = ?
     *
     * @param categoriaId ID de la categoría
     * @return Número de productos en la categoría
     */
    long countByCategoriaId(Long categoriaId);

    // Métodos adicionales que podrías necesitar:
    // List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);
    // List<Producto> findByFechaCreacionAfter(LocalDateTime fecha);
    // List<Producto> findByCategoriaOrderByPrecioAsc(Categoria categoria);
}
