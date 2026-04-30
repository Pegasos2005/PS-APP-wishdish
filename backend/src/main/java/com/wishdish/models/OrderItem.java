package com.wishdish.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. Relación con la Comanda (A qué pedido pertenece esta línea)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 2. Relación con el Producto (Qué plato han pedido)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Le damos un valor por defecto de 1 por si a alguien se le olvida poner la cantidad
    @Column(nullable = false)
    private Integer quantity = 1;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItem.ItemStatus status = OrderItem.ItemStatus.in_kitchen; // El valor por defecto

    // Las notas específicas para este plato ("Sin pepinillos", "Poco hecho", etc.)
    @Column(name = "item_notes", columnDefinition = "TEXT")
    private String itemNotes;

    @Column(columnDefinition = "TEXT")
    private String observations;

    // Precio base del producto + precio de los extras
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    // --- NUEVAS COLUMNAS PARA EL TICKET ---
    @Column(name = "added_extras", columnDefinition = "TEXT")
    private String addedExtras; // Se guardará como "Queso:1.00;Bacon:1.50"

    @Column(name = "removed_defaults", columnDefinition = "TEXT")
    private String removedDefaults; // Se guardará como "Cebolla;Tomate"

    public String getAddedExtras() { return addedExtras; }
    public void setAddedExtras(String addedExtras) { this.addedExtras = addedExtras; }

    public String getRemovedDefaults() { return removedDefaults; }
    public void setRemovedDefaults(String removedDefaults) { this.removedDefaults = removedDefaults; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    // --- NUESTRO ENUM INTERNO (Las únicas opciones válidas) ---
    public enum ItemStatus {
        in_kitchen,
        prepared
    }

    // --- Constructor vacío obligatorio ---
    public OrderItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderItem.ItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItem.ItemStatus status) {
        this.status = status;
    }

    public void advanceStatus() {
        switch (this.status) {
            case in_kitchen:
                this.status = OrderItem.ItemStatus.prepared;
                break;
            case prepared:
                break;
        }
    }

    public String getItemNotes() {
        return itemNotes;
    }

    public void setItemNotes(String itemNotes) {
        this.itemNotes = itemNotes;
    }

    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "ItemComanda{" +
                "id=" + id +
                ", producto=" + (product != null ? product.getName() : "null") +
                ", cantidad=" + quantity +
                ", estado=" + status +
                '}';
    }
}