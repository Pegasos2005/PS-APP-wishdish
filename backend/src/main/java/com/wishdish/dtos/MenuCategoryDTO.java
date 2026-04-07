package com.wishdish.dtos;

import com.wishdish.models.Product;

import java.util.List;

public class MenuCategoryDTO {

    private Integer categoryId;
    private String categoryName;
    private String categoryDescription;
    private List<ProductDTO> products; // Agora é uma lista segura de DTOs

    public MenuCategoryDTO(Integer id, String name, String description, List<Product> products) {
    }

    public MenuCategoryDTO(Integer categoryId, String categoryName, String categoryDescription, List<ProductDTO> products) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.products = products;
    }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
}