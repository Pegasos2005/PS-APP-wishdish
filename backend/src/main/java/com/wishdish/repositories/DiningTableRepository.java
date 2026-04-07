package com.wishdish.repositories;

import com.wishdish.models.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Integer> {

    Optional<DiningTable> findByTableNumber(Integer tableNumber);

    boolean existsByTableNumber(Integer tableNumber);
}