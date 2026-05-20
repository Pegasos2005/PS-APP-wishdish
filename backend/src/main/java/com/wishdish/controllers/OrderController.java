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

    // El cocinero actualiza el estado del plato (preparado o en cocina)
    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<OrderItemDTO> updateItemStatus(@PathVariable Integer itemId, @RequestParam String status) {
        OrderItem updatedItem = orderService.updateItemStatus(itemId, status);
        // Transformamos la entidad pura a DTO antes de enviarla
        return ResponseEntity.ok(new OrderItemDTO(updatedItem));
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Integer itemId) {
        orderService.removeOrderItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // NUEVO ENDPOINT: Reporte Diario de Caja
    @GetMapping("/daily-report")
    public ResponseEntity<com.wishdish.dtos.DailyReportDTO> getDailyReport() {
        return ResponseEntity.ok(orderService.getDailyReport());
    }

    // ENDPOINT: Realizar el cierre de caja definitivo
    @PostMapping("/close-cash")
    public ResponseEntity<?> executeCloseCash() {
        // 1. Validamos la regla de negocio: ¿Hay mesas abiertas?
        if (orderService.hasActiveTables()) {
            // Si hay mesas, devolvemos un mapa con el error en formato JSON para Angular
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "there are open tables");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 2. Si todo está limpio (cero mesas abiertas), procedemos a cerrar
        // Aquí iría tu lógica en el futuro para archivar el día, por ahora devolvemos OK
        return ResponseEntity.ok().build();
    }
}