package com.wishdish.dtos;

import com.wishdish.models.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDTO {

    private Integer id;
    private Integer tableNumber;
    private LocalDateTime orderDate;
    private String status;
    private String generalNotes;
    private List<OrderItemDTO> items; // Lista de DTOs

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Order order) {
        this.id = order.getId();
        this.tableNumber = order.getDiningTable() != null ? order.getDiningTable().getTableNumber() : null;
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus() != null ? order.getStatus().name() : null;
        this.generalNotes = order.getGeneralNotes();
        // Converte cada OrderItem real num OrderItemDTO
        this.items = order.getItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGeneralNotes() { return generalNotes; }
    public void setGeneralNotes(String generalNotes) { this.generalNotes = generalNotes; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}