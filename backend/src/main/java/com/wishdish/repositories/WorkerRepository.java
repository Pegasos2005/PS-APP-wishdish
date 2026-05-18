package com.wishdish.repositories;

import com.wishdish.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    List<Worker> findByActiveTrue();
}
