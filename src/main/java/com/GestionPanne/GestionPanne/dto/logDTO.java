package com.GestionPanne.GestionPanne.dto;


import java.time.LocalDateTime;

public class logDTO {

    private Long id;
    private String message;
    private LocalDateTime date;



    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
