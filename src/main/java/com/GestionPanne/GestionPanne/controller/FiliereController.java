package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FiliereRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Interface.IFiliereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Filiere")
public class FiliereController {

    @Autowired
    private IFiliereService filiereService;

    @Autowired
    private LogService logService;

    @Autowired
    private FiliereRepository filiereRepository;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FiliereDTO>> createFiliere(@RequestBody Filiere filiereDTO) {
        Response<FiliereDTO> response = new Response<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            logService.saveLog("Attempting to create new program: " + filiereDTO.getName() + " by " + email);
            response = filiereService.createFiliere(filiereDTO);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Program created successfully: " + filiereDTO.getName() + " by " + email);
            } else {
                logService.saveLog("Failed to create program: " + filiereDTO.getName() + ". Reason: " + response.getMessage());
            }

        } catch (Exception e) {
            logService.saveLog("Error while creating program: " + e.getMessage() + " by " + email);
            response.setMessage("Error while creating program: " + e.getMessage());
            response.setStatusCode(500);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<FiliereDTO>>> getAllFilieres() {
        Response<List<FiliereDTO>> response = filiereService.getAllFilieres();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getFiliereById/{id}")
    public ResponseEntity<Response<FiliereDTO>> getFiliereById(@PathVariable Long id) {
        logService.saveLog("Fetching program with ID: " + id);
        Response<FiliereDTO> response = filiereService.getFiliereById(id);

        if (response.getStatusCode() != 200) {
            logService.saveLog("Program not found with ID: " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateFiliere/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FiliereDTO>> updateFiliere(@PathVariable Long id, @RequestBody Filiere filiereDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Filiere filiere = filiereRepository.findById(id)
                    .orElseThrow(() -> new OurException("No program found with ID: " + id));

            logService.saveLog("Attempting to update program: " + filiere.getName() + " (ID: " + id + ") by " + email);

            Response<FiliereDTO> response = filiereService.updateFiliere(id, filiereDTO);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Program updated successfully: " + filiere.getName() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to update program with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error while updating program with ID: " + id + ". Error: " + e.getMessage());
            Response<FiliereDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteFiliere/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteFiliere(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Filiere filiere = filiereRepository.findById(id)
                    .orElseThrow(() -> new OurException("No program found with ID: " + id));

            logService.saveLog("Attempting to delete program: " + filiere.getName() + " (ID: " + id + ") by " + email);

            Response<Void> response = filiereService.deleteFiliere(id);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Program deleted successfully: " + filiere.getName() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to delete program with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error while deleting program with ID: " + id + ". Error: " + e.getMessage());
            Response<Void> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }
}