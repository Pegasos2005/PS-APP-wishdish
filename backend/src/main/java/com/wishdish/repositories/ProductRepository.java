package com.wishdish.repositories;

import com.wishdish.models.Category;
import com.wishdish.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(Category category);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findByAvailable(Boolean available);

    List<Product> findByAvailableTrue();

    List<Product> findByCategoryAndAvailable(Category category, Boolean available);

    List<Product> findByCategoryIdAndAvailableTrue(Integer categoryId);

    Optional<Product> findByName(String name);

    Optional<Product> findByNameIgnoreCase(String name);

    List<Product> findByNameContaining(String name);

    long countByCategoryId(Integer categoryId);
}