package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Temporal(TemporalType.DATE)
    private Date failureDate;

    @Temporal(TemporalType.DATE)
    private Date solutionDate;

    @NotBlank
    private String failureDetails;
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Etat etat;
}
