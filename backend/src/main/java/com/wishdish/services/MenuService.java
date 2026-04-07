package com.wishdish.services;

import com.wishdish.dtos.MenuCategoryDTO;
import com.wishdish.dtos.ProductDTO;
import com.wishdish.models.Category;
import com.wishdish.models.Product;
import com.wishdish.repositories.CategoryRepository;
import com.wishdish.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<MenuCategoryDTO> getFullMenu() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    List<Product> products = productRepository.findByCategoryId(category.getId());

                    // Traducimos los productos de la BD a DTOs limpios
                    List<ProductDTO> productDTOs = products.stream()
                            .map(ProductDTO::new)
                            .collect(Collectors.toList());

                    return new MenuCategoryDTO(
                            category.getId(),
                            category.getName(),
                            category.getDescription(),
                            productDTOs
                            //products
                    );
                })
                .collect(Collectors.toList());
    }

    public List<MenuCategoryDTO> getAvailableMenu() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    List<Product> availableProducts = productRepository
                            .findByCategoryIdAndAvailableTrue(category.getId());

                    // Traducimos los productos de la BD a DTOs limpios
                    List<ProductDTO> productDTOs = availableProducts.stream()
                            .map(ProductDTO::new)
                            .collect(Collectors.toList());

                    return new MenuCategoryDTO(
                            category.getId(),
                            category.getName(),
                            category.getDescription(),
                            productDTOs
                            //availableProducts
                    );
                })
                // Filtramos para no mostrar categorías que se hayan quedado sin productos
                .filter(menuCategory -> !menuCategory.getProducts().isEmpty())
                .collect(Collectors.toList());
    }

    public MenuCategoryDTO getMenuByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una categoría con el ID: " + categoryId));

        List<Product> products = productRepository.findByCategoryId(categoryId);

        /* traducimos */
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        return new MenuCategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productDTOs
                //products
        );
    }

    public MenuCategoryDTO getAvailableMenuByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una categoría con el ID: " + categoryId));

        List<Product> availableProducts = productRepository.findByCategoryIdAndAvailableTrue(categoryId);

        /* Traducimos */
        List<ProductDTO> productDTOs = availableProducts.stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());

        return new MenuCategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                productDTOs
                //availableProducts
        );
    }
}