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

    @Override
    public String toString() {
        return "Mesa{" +
                "id=" + id +
                ", numeroMesa=" + tableNumber +
                '}';
    }
}