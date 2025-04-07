package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.logDTO;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/logs")
    public ResponseEntity<Response<List<logDTO>>> getAllLogs() {
        Response<List<logDTO>> response = logService.getAllLog();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}