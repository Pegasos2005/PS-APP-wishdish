package com.wishdish.backend.repositories;

import com.wishdish.backend.models.Comanda;
import com.wishdish.backend.models.Comanda.EstadoComanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    List<Comanda> findByEstado(EstadoComanda estado);

    List<Comanda> findByEstadoNot(EstadoComanda estado);

    List<Comanda> findByEstadoIn(List<EstadoComanda> estados);
}
