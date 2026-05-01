package com.wishdish.controllers; // Ajusta el paquete según tu estructura

import com.wishdish.models.Product;
import com.wishdish.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPicture(productDetails.getPicture());
                    // Si también editas ingredientes, aquí deberías gestionar la actualización de la lista

                    Product updatedProduct = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}