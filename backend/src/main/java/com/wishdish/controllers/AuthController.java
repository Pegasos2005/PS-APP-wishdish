// src/main/java/com/wishdish/controllers/AuthController.java
package com.wishdish.controllers;

import com.wishdish.models.User;
import com.wishdish.models.Worker;
import com.wishdish.repositories.UserRepository;
import com.wishdish.repositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String pin = request.get("pin");

        // Si no mandan nombre (Login de Admin), buscamos por rol ADMIN
        if (username == null || username.trim().isEmpty()) {
            Optional<User> userOpt = userRepository.findFirstByRole(User.Role.ADMIN);

            if (userOpt.isPresent()) {
                String hashedPin = hashPassword(pin); // Encriptamos el pin que llega

                if (userOpt.get().getPinHash().equals(hashedPin)) {
                    Map<String, String> response = new HashMap<>();
                    response.put("role", userOpt.get().getRole().name());
                    response.put("name", userOpt.get().getName());
                    return ResponseEntity.ok(response);
                }
            }
        }
        else {
            Optional<Worker> workerOpt = workerRepository.findByNameAndActiveTrue(username);

            // Comparamos el PIN directamente ya que en el CRUD lo guardamos
            if (workerOpt.isPresent() && workerOpt.get().getPin().equals(pin)) {
                Map<String, String> response = new HashMap<>();
                response.put("role", workerOpt.get().getRole()); // Manda "CAMARERO" o "COCINERO"
                response.put("name", workerOpt.get().getName());
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}