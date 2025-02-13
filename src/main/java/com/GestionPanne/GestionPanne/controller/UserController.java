package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.service.Implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Créer un utilisateur
    @PostMapping
    public Response<UserDTO> createUser(@RequestBody User userDTO) {
        return userService.registre(userDTO);
    }

    // Récupérer tous les utilisateurs
    @GetMapping
    public Response<List<UserDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public Response<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public Response<UserDTO> updateUser( @RequestBody User userDTO) {
        return userService.updateUser( userDTO);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public Response<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }


}
