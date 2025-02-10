package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    @NotBlank
    private String nameProject;

    @ManyToOne
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @OneToMany(mappedBy = "project",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "project",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Problem> problems;
}
