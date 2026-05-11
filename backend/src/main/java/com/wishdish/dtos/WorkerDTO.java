package com.wishdish.dtos;

import com.wishdish.models.Worker;

public class WorkerDTO {
    private Integer id;
    private String name;
    private String role;
    private String pin;
    private Boolean active;

    public WorkerDTO() {
    }

    public WorkerDTO(Worker worker) {
        this.id = worker.getId();
        this.name = worker.getName();
        this.role = worker.getRole();
        this.pin = worker.getPin();
        this.active = worker.getActive();
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
}
