package com.wishdish.backend.repository;

import com.wishdish.backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad Categoria.
 *
 * Esta interfaz NO necesita implementación. Spring Data JPA genera automáticamente
 * todos los métodos CRUD (Create, Read, Update, Delete) en tiempo de ejecución.
 *
 * Al extender JpaRepository, heredamos métodos como:
 * - save(categoria)           -> INSERT o UPDATE
 * - findById(id)              -> SELECT por ID
 * - findAll()                 -> SELECT todas las categorías
 * - deleteById(id)            -> DELETE por ID
 * - existsById(id)            -> Verificar si existe
 * - count()                   -> Contar registros
 * - etc.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Busca una categoría por su nombre exacto.
     *
     * Spring Data JPA genera automáticamente la consulta SQL a partir del nombre del método:
     * SELECT * FROM categorias WHERE nombre = ?
     *
     * @param nombre Nombre de la categoría a buscar
     * @return Optional con la categoría si existe, Optional.empty() si no existe
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Verifica si existe una categoría con el nombre especificado.
     *
     * Consulta generada automáticamente:
     * SELECT COUNT(*) > 0 FROM categorias WHERE nombre = ?
     *
     * @param nombre Nombre de la categoría
     * @return true si existe, false si no existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca una categoría por nombre ignorando mayúsculas/minúsculas.
     *
     * Consulta generada:
     * SELECT * FROM categorias WHERE LOWER(nombre) = LOWER(?)
     *
     * Útil para validar duplicados al crear categorías
     * (ej: "Postres" vs "postres" vs "POSTRES")
     *
     * @param nombre Nombre de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    // Podrías agregar más métodos personalizados si los necesitas:
    // List<Categoria> findByDescripcionContaining(String keyword);
    // List<Categoria> findByFechaCreacionAfter(LocalDateTime fecha);
    // etc.
}
