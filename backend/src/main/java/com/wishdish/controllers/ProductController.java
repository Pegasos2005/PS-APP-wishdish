package com.wishdish.controllers; // Ajusta el paquete según tu estructura

import com.wishdish.models.Product;
import com.wishdish.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products") // Esta es la URL exacta que tu Angular está llamando
@CrossOrigin(origins = "http://localhost:4200") // Permite que Angular (4200) hable con Spring (8080)
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        // Devuelve la lista completa de productos
        return productRepository.findAll();
    }

    // Opcional: Si luego quieres filtrar por categoría, ya tienes el repo listo para esto:
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}