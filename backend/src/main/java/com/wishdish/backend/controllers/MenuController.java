package com.wishDishDevelops.backend.controllers;

import com.wishDishDevelops.backend.models.Category;
import com.wishDishDevelops.backend.services.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*") // Permite que Angular (Frontend) se conecte sin bloqueos de seguridad CORS
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // GET http://localhost:8080/api/menu
    // Historia 1: El cliente ve los platos disponibles ordenados por categorías
    @GetMapping
    public List<Category> getMenu() {
        return menuService.getFullMenu();
    }
}