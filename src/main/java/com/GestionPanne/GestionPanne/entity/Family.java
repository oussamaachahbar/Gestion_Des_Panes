package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    private String name;
}
