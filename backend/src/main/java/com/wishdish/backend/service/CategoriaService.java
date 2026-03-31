package com.wishdish.backend.service;

import com.wishdish.backend.entity.Categoria;
import com.wishdish.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para las Categorías.
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
public class CategoriaService {

    // ===========================
    // INYECCIÓN DE DEPENDENCIAS
    // ===========================

    /**
     * Repository para acceder a la base de datos.
     * @Autowired: Spring inyecta automáticamente una instancia del Repository.
     */
    @Autowired
    private CategoriaRepository categoriaRepository;

    // ===========================
    // MÉTODOS CRUD
    // ===========================

    /**
     * Crea una nueva categoría en la base de datos.
     *
     * Validaciones:
     * - El nombre no puede estar vacío
     * - No puede existir otra categoría con el mismo nombre (ignorando mayúsculas)
     *
     * @param categoria Objeto Categoria con los datos a guardar
     * @return La categoría guardada (con ID generado y fechas establecidas)
     * @throws IllegalArgumentException si el nombre está vacío o ya existe
     */
    public Categoria crearCategoria(Categoria categoria) {
        // Validación 1: El nombre no puede ser nulo o vacío
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        // Validación 2: No puede existir otra categoría con el mismo nombre
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());
        if (categoriaExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        // Si pasa las validaciones, guardamos en la base de datos
        // El Repository ejecuta: INSERT INTO categorias (nombre, descripcion, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?)
        return categoriaRepository.save(categoria);
    }

    /**
     * Obtiene todas las categorías de la base de datos.
     *
     * @return Lista con todas las categorías (vacía si no hay ninguna)
     */
    public List<Categoria> obtenerTodasLasCategorias() {
        // SELECT * FROM categorias ORDER BY id
        return categoriaRepository.findAll();
    }

    /**
     * Busca una categoría por su ID.
     *
     * @param id Identificador de la categoría
     * @return Optional con la categoría si existe, Optional.empty() si no existe
     */
    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        // SELECT * FROM categorias WHERE id = ?
        return categoriaRepository.findById(id);
    }

    /**
     * Busca una categoría por su nombre exacto.
     *
     * @param nombre Nombre de la categoría
     * @return Optional con la categoría si existe, Optional.empty() si no existe
     */
    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        // SELECT * FROM categorias WHERE nombre = ?
        return categoriaRepository.findByNombre(nombre);
    }

    /**
     * Actualiza una categoría existente.
     *
     * Validaciones:
     * - La categoría debe existir
     * - Si se cambia el nombre, no debe existir otra categoría con ese nombre
     *
     * @param id ID de la categoría a actualizar
     * @param categoriaActualizada Objeto con los nuevos datos
     * @return La categoría actualizada
     * @throws IllegalArgumentException si la categoría no existe o el nombre está duplicado
     */
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        // Validación 1: La categoría debe existir
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe una categoría con el ID: " + id);
        }

        Categoria categoria = categoriaExistente.get();

        // Validación 2: Si se cambió el nombre, verificar que no esté duplicado
        if (!categoria.getNombre().equals(categoriaActualizada.getNombre())) {
            Optional<Categoria> otraCategoriaConMismoNombre = categoriaRepository.findByNombreIgnoreCase(categoriaActualizada.getNombre());
            if (otraCategoriaConMismoNombre.isPresent()) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
            }
        }

        // Actualizar los campos
        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());

        // Guardar cambios (el @PreUpdate actualizará automáticamente fechaActualizacion)
        // UPDATE categorias SET nombre = ?, descripcion = ?, fecha_actualizacion = ? WHERE id = ?
        return categoriaRepository.save(categoria);
    }

    /**
     * Elimina una categoría por su ID.
     *
     * @param id ID de la categoría a eliminar
     * @throws IllegalArgumentException si la categoría no existe
     */
    public void eliminarCategoria(Long id) {
        // Validación: La categoría debe existir
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una categoría con el ID: " + id);
        }

        // DELETE FROM categorias WHERE id = ?
        categoriaRepository.deleteById(id);
    }

    /**
     * Cuenta el número total de categorías en la base de datos.
     *
     * @return Número de categorías
     */
    public long contarCategorias() {
        // SELECT COUNT(*) FROM categorias
        return categoriaRepository.count();
    }
}
