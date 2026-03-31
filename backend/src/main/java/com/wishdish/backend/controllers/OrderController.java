package com.wishDishDevelops.backend.controllers;

import com.wishDishDevelops.backend.dtos.OrderRequestDTO;
import com.wishDishDevelops.backend.models.Order;
import com.wishDishDevelops.backend.models.OrderItem;
import com.wishDishDevelops.backend.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET http://localhost:8080/api/orders/active
    // Historia 3: Los empleados ven las comandas activas
    @GetMapping("/active")
    public List<Order> getActiveOrders() {
        return orderService.getActiveOrders();
    }

    // POST http://localhost:8080/api/orders
    // Historia 2: El cliente le da a "Comandar" y nos envía la mesa y los platos
    @PostMapping
    public Order createOrder(@RequestBody OrderRequestDTO request) {
        return orderService.createOrder(request.getTableId(), request.getProductIds());
    }

    // PUT http://localhost:8080/api/orders/items/{itemId}/advance
    // Historia 4 y 5: El cocinero avanza el estado del plato
    @PutMapping("/items/{itemId}/advance")
    public OrderItem advanceItemStatus(@PathVariable Integer itemId) {
        return orderService.advanceItemStatus(itemId);
    }
}