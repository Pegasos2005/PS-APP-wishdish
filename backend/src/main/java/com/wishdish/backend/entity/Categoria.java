package com.wishdish.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una categoría de productos en el sistema.
 * Funciona como un catálogo/enumerado para clasificar productos.
 *
 * Ejemplos: "Entrantes", "Platos Principales", "Postres", "Bebidas"
 */
@Entity
@Table(name = "categorias")
public class Categoria {

    // ===========================
    // ATRIBUTOS
    // ===========================

    /**
     * Identificador único de la categoría (Primary Key)
     * Se genera automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la categoría
     * - Debe ser único (no puede haber dos categorías con el mismo nombre)
     * - No puede ser nulo
     * - Máximo 100 caracteres
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción opcional de la categoría
     * Ejemplo: "Platos para comenzar la comida"
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * Fecha y hora en que se creó el registro
     * Se establece automáticamente al crear la entidad
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última actualización
     * Se actualiza automáticamente cada vez que se modifica la entidad
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ===========================
    // CONSTRUCTORES
    // ===========================

    /**
     * Constructor vacío requerido por JPA
     */
    public Categoria() {
    }

    /**
     * Constructor con parámetros básicos
     * @param nombre Nombre de la categoría
     * @param descripcion Descripción de la categoría
     */
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // ===========================
    // MÉTODOS DE CICLO DE VIDA (JPA Callbacks)
    // ===========================

    /**
     * Se ejecuta automáticamente ANTES de guardar la entidad en la BD
     * Establece la fecha de creación
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Se ejecuta automáticamente ANTES de actualizar la entidad en la BD
     * Actualiza la fecha de modificación
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ===========================
    // GETTERS Y SETTERS
    // ===========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // ===========================
    // MÉTODOS AUXILIARES
    // ===========================

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}