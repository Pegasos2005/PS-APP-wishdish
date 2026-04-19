package com.wishdish.repositories;

import com.wishdish.models.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    List<ProductIngredient> findByProductId(Integer productId);
}
