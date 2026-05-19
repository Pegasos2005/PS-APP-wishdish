package com.wishdish.controllers;

import com.wishdish.dtos.UserDTO;
import com.wishdish.models.User;
import com.wishdish.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllWorkers() {
        List<UserDTO> workers = userService.getAllActiveWorkers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(workers);
    }


    @PostMapping
    public ResponseEntity<UserDTO> createWorker(@RequestBody UserDTO userDto) {
        User newUser = userService.createWorker(userDto);
        return ResponseEntity.ok(new UserDTO(newUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Integer id) {
        userService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateWorker(@PathVariable Integer id, @RequestBody UserDTO workerDetails) {
        User updatedUser = userService.updateWorker(id, workerDetails);
        return ResponseEntity.ok(new UserDTO(updatedUser));
    }
}