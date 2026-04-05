package com.wishdish.backend.repositories;

import com.wishdish.backend.models.Producto;
import com.wishdish.backend.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByDisponible(Boolean disponible);

    List<Producto> findByDisponibleTrue();

    List<Producto> findByCategoriaAndDisponible(Categoria categoria, Boolean disponible);

    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);

    Optional<Producto> findByNombre(String nombre);

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    List<Producto> findByNombreContaining(String nombre);

    long countByCategoriaId(Long categoriaId);
}
