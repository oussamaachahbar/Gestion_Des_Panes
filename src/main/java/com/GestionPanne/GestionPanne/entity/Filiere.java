package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "filieres")
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    private String name;
    private String city;
    private String country;
}
