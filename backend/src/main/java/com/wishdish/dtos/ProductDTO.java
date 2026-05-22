package com.wishdish.dtos;

import com.wishdish.models.Product;
import com.wishdish.models.Category;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String picture;
    private List<IngredientDTO> ingredients;
    private Boolean available;
    private Category category;

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.picture = product.getPicture();
        this.available = product.getAvailable();
        this.category = product.getCategory();

        // SEGURIDAD: Comprobamos si la lista existe antes de iterar
        if (product.getProductIngredients() != null) {
            this.ingredients = product.getProductIngredients().stream()
                    .filter(pi -> pi.getIngredient() != null) // <--- FILTRO CRÍTICO: Evita el NullPointerException
                    .map(pi -> {
                        IngredientDTO dto = new IngredientDTO(pi.getIngredient());
                        dto.setIsDefault(pi.getIsDefault());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            this.ingredients = new ArrayList<>();
        }
    }

    // Getters y Setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
    public List<IngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientDTO> ingredients) { this.ingredients = ingredients; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}