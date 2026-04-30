package com.wishdish.dtos;

import com.wishdish.models.OrderItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItemDTO {
    private Integer id;
    private String productName;
    private String status;
    private String itemNotes;
    private Integer quantity;
    private BigDecimal productPrice;
    private String observations;

    // NUEVAS LISTAS PARA ANGULAR
    private List<Map<String, Object>> extras;
    private List<String> removedDefaults;

    public OrderItemDTO() {}

    public OrderItemDTO(OrderItem item) {
        this.id = item.getId();
        this.status = item.getStatus().name();
        this.itemNotes = item.getItemNotes();
        this.quantity = item.getQuantity();
        this.observations = item.getObservations();

        if (item.getProduct() != null) {
            this.productName = item.getProduct().getName();
            // CUIDADO AQUÍ: Mandamos el UnitPrice (Que ya lleva la suma de los extras)
            this.productPrice = item.getUnitPrice() != null ? item.getUnitPrice() : item.getProduct().getPrice();
        }

        // Traducimos los Extras ("Queso:1.00;Bacon:1.50") a objetos para Angular
        this.extras = new ArrayList<>();
        if (item.getAddedExtras() != null && !item.getAddedExtras().isEmpty()) {
            for (String ex : item.getAddedExtras().split(";")) {
                String[] parts = ex.split(":");
                if (parts.length == 2) {
                    Map<String, Object> extraMap = new HashMap<>();
                    extraMap.put("name", parts[0]);
                    extraMap.put("price", new BigDecimal(parts[1]));
                    this.extras.add(extraMap);
                }
            }
        }

        // Traducimos los Quitados ("Cebolla;Tomate")
        this.removedDefaults = new ArrayList<>();
        if (item.getRemovedDefaults() != null && !item.getRemovedDefaults().isEmpty()) {
            this.removedDefaults = Arrays.asList(item.getRemovedDefaults().split(";"));
        }
    }

    // Getters
    public Integer getId() { return id; }
    public String getProductName() { return productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public String getStatus() { return status; }
    public String getItemNotes() { return itemNotes; }
    public Integer getQuantity() { return quantity; }
    public String getObservations() { return observations; }
    public List<Map<String, Object>> getExtras() { return extras; }
    public List<String> getRemovedDefaults() { return removedDefaults; }
}