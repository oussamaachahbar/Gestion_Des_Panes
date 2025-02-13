package com.GestionPanne.GestionPanne.controller;
import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.service.Interface.IFiliereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filieres")
public class FiliereController {

    @Autowired
    private IFiliereService filiereService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FiliereDTO>> createFiliere(@RequestBody Filiere filiereDTO) {
        Response<FiliereDTO> response=  filiereService.createFiliere(filiereDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<FiliereDTO>>> getAllFilieres() {
        Response<List<FiliereDTO>> response= filiereService.getAllFilieres();
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/getFiliereById/{id}")
    public ResponseEntity<Response<FiliereDTO>> getFiliereById(@PathVariable Long id) {
        Response<FiliereDTO> response= filiereService.getFiliereById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PutMapping("/updateFiliere/{id}")
    public ResponseEntity<Response<FiliereDTO>> updateFiliere(@PathVariable Long id, @RequestBody Filiere filiereDTO) {
        Response<FiliereDTO> response= filiereService.updateFiliere(id, filiereDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @DeleteMapping("/deleteFiliere/{id}")
    public ResponseEntity<Response<Void>> deleteFiliere(@PathVariable Long id) {
        Response<Void> response= filiereService.deleteFiliere(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}

