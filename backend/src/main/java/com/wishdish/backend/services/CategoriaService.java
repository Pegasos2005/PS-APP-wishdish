package com.wishdish.backend.services;

import com.wishdish.backend.models.Categoria;
import com.wishdish.backend.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria crearCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre());
        if (categoriaExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }

        return categoriaRepository.save(categoria);
    }

    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Optional<Categoria> categoriaExistente = categoriaRepository.findById(id);
        if (categoriaExistente.isEmpty()) {
            throw new IllegalArgumentException("No existe una categoría con el ID: " + id);
        }

        Categoria categoria = categoriaExistente.get();

        if (!categoria.getNombre().equals(categoriaActualizada.getNombre())) {
            Optional<Categoria> otraCategoriaConMismoNombre = categoriaRepository.findByNombreIgnoreCase(categoriaActualizada.getNombre());
            if (otraCategoriaConMismoNombre.isPresent()) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaActualizada.getNombre());
            }
        }

        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());

        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una categoría con el ID: " + id);
        }

        categoriaRepository.deleteById(id);
    }

    public long contarCategorias() {
        return categoriaRepository.count();
    }
}
