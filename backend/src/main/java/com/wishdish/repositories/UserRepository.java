// src/main/java/com/wishdish/repositories/UserRepository.java
package com.wishdish.repositories;

import com.wishdish.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);
    Optional<User> findFirstByRole(User.Role role);

    List<User> findByRoleNotAndActiveTrue(User.Role role);
    Optional<User> findByNameAndActiveTrue(String name);
}