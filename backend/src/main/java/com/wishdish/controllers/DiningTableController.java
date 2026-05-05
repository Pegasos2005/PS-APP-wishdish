// src/main/java/com/wishdish/controllers/DiningTableController.java
package com.wishdish.controllers;

import com.wishdish.repositories.DiningTableRepository;
import com.wishdish.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class DiningTableController {

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private OrderService orderService;

    // GET http://localhost:8080/api/tables/5/exists
    @GetMapping("/{tableNumber}/exists")
    public ResponseEntity<Boolean> checkTableExists(@PathVariable Integer tableNumber) {
        // Busca si hay alguna mesa con ese número. isPresent() devuelve true o false.
        boolean exists = diningTableRepository.findByTableNumber(tableNumber).isPresent();
        return ResponseEntity.ok(exists);
    }

    // PUT /api/tables/3/request-payment — el cliente pide pagar
    @PutMapping("/{tableNumber}/request-payment")
    public ResponseEntity<Void> requestPayment(@PathVariable Integer tableNumber) {
        orderService.requestPayment(tableNumber);
        return ResponseEntity.ok().build();
    }

    // PUT /api/tables/3/close — el camarero cierra la mesa (cobra)
    @PutMapping("/{tableNumber}/close")
    public ResponseEntity<Void> closeTable(@PathVariable Integer tableNumber) {
        orderService.closeTable(tableNumber);
        return ResponseEntity.ok().build();
    }

    // GET /api/tables/payment-requested — números de mesa esperando pago
    @GetMapping("/payment-requested")
    public ResponseEntity<List<Integer>> getTablesAwaitingPayment() {
        return ResponseEntity.ok(orderService.getTablesAwaitingPayment());
    }

    // GET /api/tables/3/status — estado para el polling de la tablet
    @GetMapping("/{tableNumber}/status")
    public ResponseEntity<Map<String, Boolean>> getTableStatus(@PathVariable Integer tableNumber) {
        return ResponseEntity.ok(Map.of(
                "paymentRequested", orderService.isPaymentRequested(tableNumber),
                "hasActiveOrders", orderService.tableHasActiveOrders(tableNumber)
        ));
    }
}
