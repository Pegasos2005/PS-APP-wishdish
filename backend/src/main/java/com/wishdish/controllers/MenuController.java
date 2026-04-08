package com.wishdish.controllers;

import com.wishdish.dtos.MenuCategoryDTO;
import com.wishdish.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // GET http://localhost:8080/api/menu
    @GetMapping
    public ResponseEntity<List<MenuCategoryDTO>> getFullMenu() {
        List<MenuCategoryDTO> menu = menuService.getFullMenu();
        return ResponseEntity.ok(menu);
    }

    // GET http://localhost:8080/api/menu/available
    @GetMapping("/available")
    public ResponseEntity<List<MenuCategoryDTO>> getAvailableMenu() {
        List<MenuCategoryDTO> menu = menuService.getAvailableMenu();
        return ResponseEntity.ok(menu);
    }

    // GET http://localhost:8080/api/menu/category/{categoryId}
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getMenuByCategory(@PathVariable Integer categoryId) {
        try {
            MenuCategoryDTO menuCategory = menuService.getMenuByCategory(categoryId);
            return ResponseEntity.ok(menuCategory);
        } catch (IllegalArgumentException e) {
            // Si no existe la categoría, devolvemos un error 404 limpio
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // GET http://localhost:8080/api/menu/category/{categoryId}/available
    @GetMapping("/category/{categoryId}/available")
    public ResponseEntity<?> getAvailableMenuByCategory(@PathVariable Integer categoryId) {
        try {
            MenuCategoryDTO menuCategory = menuService.getAvailableMenuByCategory(categoryId);
            return ResponseEntity.ok(menuCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}