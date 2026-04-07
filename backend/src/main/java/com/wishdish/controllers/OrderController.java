package com.wishdish.controllers;

import com.wishdish.dtos.OrderRequestDTO;
import com.wishdish.dtos.OrderResponseDTO;
import com.wishdish.dtos.OrderItemDTO;
import com.wishdish.models.Order;
import com.wishdish.models.OrderItem;
import com.wishdish.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // GET http://localhost:8080/api/orders/active
    // Historia 3: Los empleados ven las comandas activas
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponseDTO>> getActiveOrders() {
        List<OrderResponseDTO> activeOrders = orderService.getActiveOrders();
        return ResponseEntity.ok(activeOrders);
    }

    // POST http://localhost:8080/api/orders
    // Historia 2: El cliente le da a "Comandar" y nos envía la mesa y los platos
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
        Order newOrder = orderService.createOrder(request.getTableId(), request.getProductIds());
        // Transformamos la entidad pura a DTO antes de enviarla al frontend
        return ResponseEntity.ok(new OrderResponseDTO(newOrder));
    }

    // PUT http://localhost:8080/api/orders/items/{itemId}/advance
    // Historia 4 y 5: El cocinero avanza el estado del plato
    @PutMapping("/items/{itemId}/advance")
    public ResponseEntity<OrderItemDTO> advanceItemStatus(@PathVariable Integer itemId) {
        OrderItem advancedItem = orderService.advanceItemStatus(itemId);
        // Transformamos la entidad pura a DTO antes de enviarla
        return ResponseEntity.ok(new OrderItemDTO(advancedItem));
    }
}