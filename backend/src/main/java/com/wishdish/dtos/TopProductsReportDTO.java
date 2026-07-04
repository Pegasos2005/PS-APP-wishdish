package com.wishdish.dtos;
import java.util.List;

public class TopProductsReportDTO {
    private String range;
    private List<TopProductDTO> products;

    // Getters y Setters
    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }
    public List<TopProductDTO> getProducts() { return products; }
    public void setProducts(List<TopProductDTO> products) { this.products = products; }
}
