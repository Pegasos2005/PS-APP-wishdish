package com.wishdish.models;

import jakarta.persistence.*;

// Especifica a Spring Boot q es una clase referente a una tabla
@Entity
// Dice exactamente con qué tabla de MySQL tiene que emparejarse. Si no pones esto, Java buscaría una tabla llamada "category" y daría error.
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 1. Constructor vacío (Obligatorio para que Hibernate funcione)
    public Category() {
    }

    // 2. Constructor con parámetros (Opcional, pero muy útil)
    public Category(String name) {
        this.name = name;
    }

    // 3. Getters y Setters (Para que Java pueda leer y modificar las variables privadas)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
