package com.wishdish.controllers;

import com.wishdish.dtos.ManualItemRequestDTO;
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
public class OrderController {

    @Autowired
    private OrderService orderService;


    // Los empleados ven las comandas activas
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponseDTO>> getActiveOrders() {
        List<OrderResponseDTO> activeOrders = orderService.getActiveOrders();
        return ResponseEntity.ok(activeOrders);
    }

    //El cliente le da a "Comandar" y nos envía la mesa y los platos
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
        Order newOrder = orderService.createOrder(request.getTableId(), request.getItems(), request.getGeneralNotes());
        // Transformamos la entidad pura a DTO antes de enviarla al frontend
        return ResponseEntity.ok(new OrderResponseDTO(newOrder));
    }

    // El cocinero avanza el estado del plato
    @PutMapping("/items/{itemId}/advance")
    public ResponseEntity<OrderItemDTO> advanceItemStatus(@PathVariable Integer itemId) {
        OrderItem advancedItem = orderService.advanceItemStatus(itemId);
        // Transformamos la entidad pura a DTO antes de enviarla
        return ResponseEntity.ok(new OrderItemDTO(advancedItem));
    }


    // endpoint para que el camarero finalice la comanda entera
    @PutMapping("/{orderId}/advance")
    public ResponseEntity<OrderResponseDTO> advanceOrderStatus(@PathVariable Integer orderId) {
        Order updatedOrder = orderService.advanceOrderStatus(orderId);
        return ResponseEntity.ok(new OrderResponseDTO(updatedOrder));
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<OrderResponseDTO>> getTableTicket(@PathVariable Integer tableId) {
        List<OrderResponseDTO> orders = orderService.getActiveOrdersByTable(tableId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<?> addManualItem(@PathVariable Integer orderId, @RequestBody ManualItemRequestDTO request) {
        try {
            List<OrderResponseDTO> updatedTableOrders = orderService.addManualItemToOrder(orderId, request);
            return ResponseEntity.ok(updatedTableOrders);
        } catch (RuntimeException e) {
            // Si es el error de "producto no disponible", devolvemos un 400 con el mensaje
            if (e.getMessage().contains("no está disponible actualmente")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            throw e;
        }
    }
}