package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.LoginRequest;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

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
}
