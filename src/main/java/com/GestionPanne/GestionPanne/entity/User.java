package com.GestionPanne.GestionPanne.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String password;

    private String name;
    @Column(unique = true)
    private String cin;

    private String email;
    private String phone;
    private String role;
    private Boolean isSupervisor;
    @Column(nullable = false)
    private Boolean isTechnicien = false;    private boolean status=false;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = true)
    private Post post;

    public Post getPost() {
        return "ADMIN".equals(this.role) ? null : post;
    }

    public void setPost(Post post) {
        if (!"ADMIN".equals(this.role)) {
            this.post = post;
        } else {
            this.post = null;
        }
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Problem> problems;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
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

    public boolean isSupervisor() {
        return !"ADMIN".equals(this.role) && Boolean.TRUE.equals(this.isSupervisor);
    }

    public boolean isTechnicien() {
        return !"ADMIN".equals(this.role) && Boolean.TRUE.equals(this.isTechnicien);
    }
}