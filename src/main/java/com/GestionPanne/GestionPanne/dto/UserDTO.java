package com.GestionPanne.GestionPanne.dto;


import lombok.Data;

@Data
public class UserDTO {
    private Double id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean isSupervisor;
}
