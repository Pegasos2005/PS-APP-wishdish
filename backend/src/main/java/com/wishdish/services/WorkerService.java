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

    @Transactional
    public void deleteWorker(Integer id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + id + " not found"));

        // Hacemos borrado
        worker.setActive(false);
        workerRepository.save(worker);
    }

    @Transactional
    public Worker updateWorker(Integer id, Worker workerDetails) {
        Worker existingWorker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + id + " not found"));

        // Actualizamos los campos
        existingWorker.setName(workerDetails.getName());
        existingWorker.setRole(workerDetails.getRole());

        // Solo actualizamos el PIN si nos envían uno nuevo
        if (workerDetails.getPin() != null && !workerDetails.getPin().isEmpty()) {
            existingWorker.setPin(workerDetails.getPin());
        }

        return workerRepository.save(existingWorker);
    }
}
