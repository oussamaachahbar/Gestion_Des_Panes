

package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Double id;
    @NotBlank
    private String password;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }




    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}



