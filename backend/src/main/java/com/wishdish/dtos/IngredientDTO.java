package com.wishdish.dtos;

import com.wishdish.models.Ingredient;

import java.math.BigDecimal;

public class IngredientDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Boolean available;
    private BigDecimal extraPrice;

    public IngredientDTO() {
    }

    public IngredientDTO(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.description = ingredient.getDescription();
        this.extraPrice = ingredient.getExtraPrice();
        this.available = ingredient.isAvailable();
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public BigDecimal getExtraPrice() { return extraPrice; }

    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }

    public Boolean getAvailable() { return available; }

    public void setAvailable(Boolean available) { this.available = available; }

}
