package com.wishdish.backend.repositories;

import com.wishdish.backend.models.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumeroMesa(Integer numeroMesa);

    boolean existsByNumeroMesa(Integer numeroMesa);
}
