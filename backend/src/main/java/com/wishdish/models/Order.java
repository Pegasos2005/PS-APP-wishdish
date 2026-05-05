package com.wishdish.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable diningTable;

    // Fechas en Java moderno. insertable = false hace que MySQL ponga la fecha automáticamente
    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.in_kitchen;

    @Column(name = "general_notes", columnDefinition = "TEXT")
    private String generalNotes;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    public enum OrderStatus {
        in_kitchen,
        served,
        paid
    }

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DiningTable getDiningTable() {
        return diningTable;
    }

    public void setDiningTable(DiningTable diningTable) {
        this.diningTable = diningTable;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void advanceStatus() {
        switch (this.status) {
            case in_kitchen:
                this.status = OrderStatus.served;
                break;
            case served:
                this.status = OrderStatus.paid;
                break;
            case paid:
                break;
        }
    }

    public String getGeneralNotes() {
        return generalNotes;
    }

    public void setGeneralNotes(String generalNotes) {
        this.generalNotes = generalNotes;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Comanda{" +
                "id=" + id +
                ", mesa=" + (diningTable != null ? diningTable.getTableNumber() : "null") +
                ", fechaComanda=" + orderDate +
                ", estado=" + status +
                ", items=" + items.size() +
                '}';
    }
}