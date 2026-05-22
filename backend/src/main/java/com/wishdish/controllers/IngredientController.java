package com.wishdish.controllers;

import com.wishdish.models.Ingredient;
import com.wishdish.models.OrderItem;
import com.wishdish.models.ProductIngredient;
import com.wishdish.repositories.IngredientRepository;
import com.wishdish.repositories.OrderItemRepository;
import com.wishdish.repositories.ProductIngredientRepository; // Asegúrate de importar esto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:4200")
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductIngredientRepository productIngredientRepository; // Inyectado para limpiar relaciones

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Integer id) {
        return ingredientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Integer id, @RequestBody Ingredient ingredientDetails) {
        return ingredientRepository.findById(id).map(existingIngredient -> {
            existingIngredient.setName(ingredientDetails.getName());
            existingIngredient.setAvailable(ingredientDetails.isAvailable());

            if (ingredientDetails.getExtraPrice() != null) {
                existingIngredient.setExtraPrice(ingredientDetails.getExtraPrice());
            }

            Ingredient updatedIngredient = ingredientRepository.save(existingIngredient);
            return ResponseEntity.ok(updatedIngredient);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) {
        // 1. Buscamos el nombre del ingrediente antes de borrarlo (necesario para el OrderItem)
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));
        String nameToRemove = ingredient.getName();

        // 2. Limpieza de OrderItem (Esto sigue siendo necesario porque no es JPA)
        List<OrderItem> affectedItems = orderItemRepository.findByAddedExtrasContaining(nameToRemove);
        for (OrderItem item : affectedItems) {
            if (item.getAddedExtras() != null) {
                String cleanedExtras = cleanExtrasString(item.getAddedExtras(), nameToRemove);
                item.setAddedExtras(cleanedExtras);
                orderItemRepository.save(item);
            }
        }

        // 3. Borrado explícito de los hijos (ProductIngredient)
        // Esto evita que Hibernate intente validar las relaciones automáticamente
        productIngredientRepository.deleteByIngredientId(id);

        // 4. Borrado final del ingrediente por ID
        ingredientRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Método auxiliar para limpiar la cadena de extras
    private String cleanExtrasString(String extras, String name) {
        if (extras == null || extras.isEmpty()) return "";

        String[] parts = extras.split(";");

        String result = Arrays.stream(parts)
                .filter(part -> !part.startsWith(name + ":"))
                .collect(Collectors.joining(";"));

        return result;
    }
}