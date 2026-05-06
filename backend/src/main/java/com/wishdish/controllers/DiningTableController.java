// src/main/java/com/wishdish/controllers/DiningTableController.java
package com.wishdish.controllers;

import com.wishdish.repositories.DiningTableRepository;
import com.wishdish.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> getTableStatus(@PathVariable Integer tableNumber) {
        Map<String, Object> status = new HashMap<>();
        status.put("paymentRequested", orderService.isPaymentRequested(tableNumber));
        status.put("hasActiveOrders", orderService.tableHasActiveOrders(tableNumber));
        status.put("reassignTo", orderService.getPendingReassignTo(tableNumber));
        return ResponseEntity.ok(status);
    }

    // POST /api/tables/5/reassign?to=8 — admin reasigna la mesa 5 a la 8
    @PostMapping("/{from}/reassign")
    public ResponseEntity<Void> reassignTable(@PathVariable Integer from, @RequestParam Integer to) {
        orderService.reassignTable(from, to);
        return ResponseEntity.ok().build();
    }

    // PUT /api/tables/5/ack-reassign — la tablet confirma que ya se movió
    @PutMapping("/{from}/ack-reassign")
    public ResponseEntity<Void> acknowledgeReassign(@PathVariable Integer from) {
        orderService.acknowledgeReassign(from);
        return ResponseEntity.ok().build();
    }

    // GET /api/tables/occupancy — listado de mesas con flag libre/ocupada
    @GetMapping("/occupancy")
    public ResponseEntity<List<Map<String, Object>>> getOccupancy() {
        return ResponseEntity.ok(orderService.getOccupancy());
    }
}
