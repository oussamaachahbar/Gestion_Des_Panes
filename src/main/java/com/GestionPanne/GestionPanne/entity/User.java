package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean isSupervisor;

    // Relation avec Post (Chaque user appartient à un seul post)
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Relation avec Problem (Un utilisateur peut signaler plusieurs pannes)
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<Problem> problems;
}
