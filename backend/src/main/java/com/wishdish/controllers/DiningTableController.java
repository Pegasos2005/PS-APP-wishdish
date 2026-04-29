// src/main/java/com/wishdish/controllers/DiningTableController.java
package com.wishdish.controllers;

import com.wishdish.repositories.DiningTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tables")
public class DiningTableController {

    @Autowired
    private DiningTableRepository diningTableRepository;

    // GET http://localhost:8080/api/tables/5/exists
    @GetMapping("/{tableNumber}/exists")
    public ResponseEntity<Boolean> checkTableExists(@PathVariable Integer tableNumber) {
        // Busca si hay alguna mesa con ese número. isPresent() devuelve true o false.
        boolean exists = diningTableRepository.findByTableNumber(tableNumber).isPresent();
        return ResponseEntity.ok(exists);
    }
}