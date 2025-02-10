package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "families")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "family",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Project> projects;
}
