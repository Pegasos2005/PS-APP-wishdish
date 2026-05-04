package com.wishdish.models;

import jakarta.persistence.*;

@Entity
@Table(name = "dining_tables")
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // unique = true: ¡Nadie puede crear dos mesas con el número 4!
    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;

    @Column(name = "payment_requested", nullable = false)
    private boolean paymentRequested = false;

    // Si la mesa ha sido reasignada, guarda el número de la mesa destino
    // para que la tablet del cliente lo detecte por polling y se mueva sola.
    @Column(name = "pending_reassign_to")
    private Integer pendingReassignTo;

    public DiningTable() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isPaymentRequested() {
        return paymentRequested;
    }

    public void setPaymentRequested(boolean paymentRequested) {
        this.paymentRequested = paymentRequested;
    }

    public Integer getPendingReassignTo() {
        return pendingReassignTo;
    }

    public void setPendingReassignTo(Integer pendingReassignTo) {
        this.pendingReassignTo = pendingReassignTo;
    }

    @Override
    public String toString() {
        return "Mesa{" +
                "id=" + id +
                ", numeroMesa=" + tableNumber +
                '}';
    }
}