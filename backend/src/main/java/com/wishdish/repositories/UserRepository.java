// src/main/java/com/wishdish/repositories/UserRepository.java
package com.wishdish.repositories;

import com.wishdish.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Te dejo preparado este método porque lo necesitaremos para el Login
    Optional<User> findByName(String name);
}