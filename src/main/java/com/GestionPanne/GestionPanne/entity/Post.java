package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    private String name;
    private String description;
    private String position;

    // Relation avec Project (Un poste appartient à un projet)
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Relation avec Users (Un poste contient plusieurs utilisateurs)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<User> users;

    // Relation avec Problems (Un poste peut avoir plusieurs pannes)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Problem> problems;
}
