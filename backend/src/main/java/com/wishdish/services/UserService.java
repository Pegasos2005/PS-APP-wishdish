package com.wishdish.services;

import com.wishdish.dtos.UserDTO;
import com.wishdish.models.User;
import com.wishdish.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // Leer todos los trabajadores activos
    public List<User> getAllActiveWorkers() {
        return userRepository.findByRoleNotAndActiveTrue(User.Role.ADMIN);
    }

    // Crear un trabajador nuevo
    @Transactional
    public User createWorker(UserDTO userDto) {
        User user = new User();
        user.setName(userDto.getName());

        try {
            User.Role role = User.Role.valueOf(userDto.getRole().toUpperCase());
            if (role == User.Role.ADMIN) role = User.Role.WAITER;
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            user.setRole(User.Role.WAITER);
        }

        user.setPinHash(hashPassword(userDto.getPin()));
        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteWorker(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + id + " not found"));

        // Hacemos borrado
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    public User updateWorker(Integer id, UserDTO workerDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker with ID " + id + " not found"));

        // Actualizamos los campos
        existingUser.setName(workerDetails.getName());

        try {
            User.Role role = User.Role.valueOf(workerDetails.getRole().toUpperCase());
            if (role == User.Role.ADMIN) role = User.Role.WAITER;
            existingUser.setRole(role);
        } catch (IllegalArgumentException e) {
            // Se queda con el rol que ya tenía si hay error
        }

        // Si desde Angular nos mandan un PIN nuevo (no vacío), lo encriptamos y guardamos
        if (workerDetails.getPin() != null && !workerDetails.getPin().trim().isEmpty()) {
            existingUser.setPinHash(hashPassword(workerDetails.getPin()));
        }

        return userRepository.save(existingUser);
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
