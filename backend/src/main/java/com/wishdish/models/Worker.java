package com.wishdish.models;

import jakarta.persistence.*;

@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    // Guardaremos el rol como texto: "CAMARERO", "COCINERO", "ADMIN"
    @Column(nullable = false, length = 50)
    private String role;

    // Un PIN de acceso rápido para sus pantallas (ej: "1234")
    @Column(length = 20)
    private String pin;

    // Para poder dar de baja a un trabajador sin borrar su historial de comandas
    @Column(nullable = false)
    private Boolean active = true;

    public Worker() {
    }

    public Worker(String name, String role, String pin) {
        this.name = name;
        this.role = role;
        this.pin = pin;
        this.active = true;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
