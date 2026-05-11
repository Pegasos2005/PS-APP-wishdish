package com.wishdish.controllers;

import com.wishdish.dtos.WorkerDTO;
import com.wishdish.models.Worker;
import com.wishdish.services.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:4200")
public class WorkerController {

    @Autowired
    private WorkerService workerService;


    @GetMapping
    public ResponseEntity<List<WorkerDTO>> getAllWorkers() {
        List<WorkerDTO> workers = workerService.getAllActiveWorkers().stream()
                .map(WorkerDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(workers);
    }


    @PostMapping
    public ResponseEntity<WorkerDTO> createWorker(@RequestBody Worker worker) {
        Worker newWorker = workerService.createWorker(worker);
        return ResponseEntity.ok(new WorkerDTO(newWorker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Integer id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerDTO> updateWorker(@PathVariable Integer id, @RequestBody Worker workerDetails) {
        Worker updatedWorker = workerService.updateWorker(id, workerDetails);
        return ResponseEntity.ok(new WorkerDTO(updatedWorker));
    }
}