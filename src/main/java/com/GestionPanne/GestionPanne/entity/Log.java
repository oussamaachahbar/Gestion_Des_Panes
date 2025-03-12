package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "log")
@Data
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String action;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Log() {}

    public Log(String action) {
        this.action = action;
    }

    }


