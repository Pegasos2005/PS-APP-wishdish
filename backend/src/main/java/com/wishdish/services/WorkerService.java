package com.wishdish.services;

import com.wishdish.models.Worker;
import com.wishdish.repositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerService {
    @Autowired
    private WorkerRepository workerRepository;

    // Leer todos los trabajadores activos
    public List<Worker> getAllActiveWorkers() {
        return workerRepository.findByActiveTrue();
    }

    // Crear un trabajador nuevo
    @Transactional
    public Worker createWorker(Worker worker) {
        // Por defecto nos aseguramos de que nazca activo
        worker.setActive(true);
        return workerRepository.save(worker);
    }
}
