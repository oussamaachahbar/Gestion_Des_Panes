package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.LoginRequest;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.repo.UserRepository;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add/{postId}")
    public ResponseEntity<Response<UserDTO>> createUser(@PathVariable Long postId, @RequestBody User user) {
        Response<UserDTO> response = userService.registreUser(postId, user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestBody LoginRequest loginRequest) {
        Response<String> response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
            }

            String username = authentication.getName();
            Optional<User> optionalUser = userRepository.findByEmail(username);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setStatus(false);
                userRepository.save(user);
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok("Logout successful. Status reset.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout error: " + e.getMessage());
        }
    }
}