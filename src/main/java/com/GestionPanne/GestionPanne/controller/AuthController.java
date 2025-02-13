package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.LoginRequest;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.User;
import com.GestionPanne.GestionPanne.service.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response<UserDTO>> register(@RequestBody User user) {
        Response<UserDTO> response = userService.registre(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestBody LoginRequest loginRequest) {
        Response<String> response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
