package com.wishdish.repositories;

import com.wishdish.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    List<Worker> findByActiveTrue();

    Optional<Worker> findByNameAndActiveTrue(String name);
}
