package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.service.Implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Créer un utilisateur
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<UserDTO>> createUser(@RequestBody User userDTO) {
        Response<UserDTO> response= userService.registre(userDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
    @GetMapping("/all")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        Response <List<UserDTO>> response=  userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id) {
        Response<UserDTO> response= userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<Response<UserDTO>> updateUser( @RequestBody User userDTO) {
        Response<UserDTO> response= userService.updateUser( userDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        Response<Void> response= userService.deleteUser(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
