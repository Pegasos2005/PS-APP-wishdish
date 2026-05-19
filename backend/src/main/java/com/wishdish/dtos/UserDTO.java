package com.wishdish.dtos;

import com.wishdish.models.User;

public class UserDTO {
    private Integer id;
    private String name;
    private String role;
    private String pin;
    private Boolean active;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole().name();
        this.pin = "";
        this.active = user.getActive();
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
