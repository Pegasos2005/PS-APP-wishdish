package com.wishdish.controllers;

import com.wishdish.dtos.ProductDTO;
import com.wishdish.models.Product;
import com.wishdish.models.ProductIngredient;
import com.wishdish.models.Ingredient;
import com.wishdish.repositories.IngredientRepository;
import com.wishdish.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    // Obtener todos
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(new ProductDTO(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear Producto
    @PostMapping
    @Transactional
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        if (product.getProductIngredients() != null) {
            for (ProductIngredient pi : product.getProductIngredients()) {
                pi.setProduct(product);
            }
        }
        return ResponseEntity.ok(new ProductDTO(productRepository.save(product)));
    }

    // Actualizar Producto - Implementación Robusta
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id, @RequestBody Product productDetails) {
        return productRepository.findById(id).map(existingProduct -> {

            // 1. Actualizar datos básicos
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPicture(productDetails.getPicture());
            existingProduct.setCategory(productDetails.getCategory());

            // 2. Gestión de Ingredientes
            if (productDetails.getProductIngredients() != null) {
                // Identificar los IDs de ingredientes que vienen en la petición
                Set<Integer> incomingIds = productDetails.getProductIngredients().stream()
                        .filter(pi -> pi.getIngredient() != null && pi.getIngredient().getId() != null)
                        .map(pi -> pi.getIngredient().getId())
                        .collect(Collectors.toSet());

                // Eliminar de la colección actual los que NO están en la nueva lista
                existingProduct.getProductIngredients().removeIf(pi ->
                        !incomingIds.contains(pi.getIngredient().getId()));

                for (ProductIngredient piRequest : productDetails.getProductIngredients()) {
                    if (piRequest.getIngredient() != null && piRequest.getIngredient().getId() != null) {
                        Integer ingredientId = piRequest.getIngredient().getId();

                        // Buscar si ya existe en la lista que nos quedó
                        ProductIngredient existingPi = existingProduct.getProductIngredients().stream()
                                .filter(pi -> pi.getIngredient().getId().equals(ingredientId))
                                .findFirst().orElse(null);

                        if (existingPi != null) {
                            existingPi.setDefault(piRequest.isDefault());
                        } else {
                            Ingredient ingredient = ingredientRepository.findById(ingredientId)
                                    .orElseThrow(() -> new RuntimeException("Ingrediente con ID " + ingredientId + " no encontrado"));
                            existingProduct.getProductIngredients().add(new ProductIngredient(existingProduct, ingredient, piRequest.isDefault()));
                        }
                    }
                }
            } else {
                existingProduct.getProductIngredients().clear();
            }

            // 3. Guardamos y devolvemos DTO para evitar recursión
            return ResponseEntity.ok(new ProductDTO(productRepository.save(existingProduct)));

        }).orElse(ResponseEntity.notFound().build());
    }

    // Borrar Producto
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // --- SISTEMA DE GESTIÓN DE IMÁGENES ---

    // Carpeta donde se guardarán las fotos (se creará en la raíz del proyecto Backend)
    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Crear carpeta si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generar nombre único (para que no se pisen fotos con el mismo nombre)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 3. Guardar el archivo físicamente
            Files.copy(file.getInputStream(), filePath);

            // 4. Devolver la URL donde se podrá ver la foto (puerto 8080)
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", "http://localhost:8080/api/products/images/" + fileName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Actúa como servidor de imágenes
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}