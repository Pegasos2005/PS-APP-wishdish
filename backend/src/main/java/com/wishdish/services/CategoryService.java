package com.wishdish.services;

import com.wishdish.models.Category;
import com.wishdish.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(category.getName());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + category.getName());
        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category updateCategory(Integer id, Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una categoría con el ID: " + id));

        // Si el nombre ha cambiado, verificamos que no colisione con otro existente
        if (!existingCategory.getName().equals(updatedCategory.getName())) {
            Optional<Category> anotherCategoryWithSameName = categoryRepository.findByNameIgnoreCase(updatedCategory.getName());
            if (anotherCategoryWithSameName.isPresent()) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + updatedCategory.getName());
            }
        }

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setDescription(updatedCategory.getDescription());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una categoría con el ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public long countCategories() {
        return categoryRepository.count();
    }
}