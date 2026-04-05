package com.wishdish.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto del menú en el sistema.
 * Los productos están clasificados por categorías.
 *
 * Ejemplos: "Hamburguesa Clásica", "Ensalada César", "Coca-Cola"
 */
@Entity
@Table(name = "productos")
public class Producto {

    // ===========================
    // ATRIBUTOS
    // ===========================

    /**
     * Identificador único del producto (Primary Key)
     * Se genera automáticamente por la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto
     * - No puede ser nulo
     * - Máximo 100 caracteres
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción detallada del producto
     * Ejemplo: "Hamburguesa de carne de res con queso cheddar, lechuga, tomate y salsa especial"
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Precio del producto
     * - No puede ser nulo
     * - Precisión: 10 dígitos totales, 2 decimales
     * Ejemplo: 12.50
     */
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * URL o ruta de la imagen del producto
     * Ejemplo: "assets/hamburguesa-clasica.jpg"
     */
    @Column(name = "imagen", length = 255)
    private String imagen;

    /**
     * Indica si el producto está disponible para la venta
     * - true: Disponible
     * - false: No disponible (agotado, temporalmente fuera de menú)
     */
    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;

    /**
     * Categoría a la que pertenece el producto
     * Relación ManyToOne: Muchos productos pueden pertenecer a una categoría
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

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
    public Producto() {
    }

    /**
     * Constructor con parámetros básicos
     * @param nombre Nombre del producto
     * @param descripcion Descripción del producto
     * @param precio Precio del producto
     * @param categoria Categoría a la que pertenece
     */
    public Producto(String nombre, String descripcion, BigDecimal precio, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = true;
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
        if (this.disponible == null) {
            this.disponible = true;
        }
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", imagen='" + imagen + '\'' +
                ", disponible=" + disponible +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
