package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/add/{postId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> createUser(@PathVariable Long postId, @RequestBody User user) {
        Response<UserDTO> response = userService.registre(postId, user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        Response<List<UserDTO>> response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id) {
        Response<UserDTO> response = userService.getUserById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<UserDTO>> updateUser(@PathVariable Long id, @RequestBody User user) {
        Response<UserDTO> response = userService.updateUser(id, user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable Long id) {
        Response<Void> response = userService.deleteUser(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @GetMapping("/get-logged-in-profile-info")
    public ResponseEntity<Response<UserDTO>> getLoggedInUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Response<UserDTO> response = userService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
