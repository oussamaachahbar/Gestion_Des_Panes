package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    private String nameProject;

    // Relation avec Family (Un projet appartient à une famille)
    @ManyToOne
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    // Relation avec Filiere (Un projet appartient à une filière)
    @ManyToOne
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    // Relation avec Post (Un projet contient plusieurs postes)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Post> posts;
}
