package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "filieres")
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @OneToMany(mappedBy = "filiere",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Project> projects;
}
