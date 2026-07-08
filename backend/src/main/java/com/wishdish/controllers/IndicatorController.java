package com.wishdish.controllers;

import com.wishdish.services.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/indicators")
public class IndicatorController {

    @Autowired
    private IndicatorService indicatorService;

    // Cada indicador tiene su propio endpoint: si uno falla, el resto del panel sigue operativo
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(@RequestParam(defaultValue = "daily") String range) {
        try {
            return ResponseEntity.ok(indicatorService.getRevenue(range));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo calcular la facturación."));
        }
    }

    @GetMapping("/top-products")
    public ResponseEntity<?> getTopProducts(@RequestParam(defaultValue = "daily") String range) {
        try {
            return ResponseEntity.ok(indicatorService.getTopProducts(range));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo calcular el ranking de productos."));
        }
    }

    @GetMapping("/orders-by-slot")
    public ResponseEntity<?> getOrdersBySlot(@RequestParam(defaultValue = "daily") String range) {
        try {
            return ResponseEntity.ok(indicatorService.getOrdersBySlot(range));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo calcular la distribución por franjas."));
        }
    }
}
