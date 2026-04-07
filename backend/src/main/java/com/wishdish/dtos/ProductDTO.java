package com.wishdish.dtos;

import com.wishdish.models.Product;
import java.math.BigDecimal;

public class ProductDTO {

    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String picture;

    public ProductDTO() {
    }

    // O construtor mágico que traduz a Entidade para DTO
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.picture = product.getPicture();
    }

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
}