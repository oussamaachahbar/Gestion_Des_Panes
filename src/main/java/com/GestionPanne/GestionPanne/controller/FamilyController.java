package com.GestionPanne.GestionPanne.controller;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FamilyRepository;
import com.GestionPanne.GestionPanne.service.Implementation.LogService;
import com.GestionPanne.GestionPanne.service.Interface.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Family")
public class FamilyController {

    @Autowired
    private IFamilyService familyService;

    @Autowired
    private LogService logService;

    @Autowired
    private FamilyRepository familyRepository;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FamilyDTO>> createFamily(@RequestBody Family familyDTO) {
        Response<FamilyDTO> response = new Response<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            logService.saveLog("Attempting to create new family: " + familyDTO.getName() + " by " + email);
            response = familyService.createFamily(familyDTO);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Family created successfully: " + familyDTO.getName() + " by " + email);
            } else {
                logService.saveLog("Failed to create family: " + familyDTO.getName() + ". Reason: " + response.getMessage());
            }

        } catch (Exception e) {
            logService.saveLog("Error while creating family: " + e.getMessage() + " by " + email);
            response.setMessage("Error while creating family: " + e.getMessage());
            response.setStatusCode(500);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response<List<FamilyDTO>>> getAllFamilies() {

        Response<List<FamilyDTO>> response = familyService.getAllFamilies();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/getFamilyById/{id}")
    public ResponseEntity<Response<FamilyDTO>> getFamilyById(@PathVariable Long id) {
        logService.saveLog("Fetching family with ID: " + id);
        Response<FamilyDTO> response = familyService.getFamilyById(id);

        if (response.getStatusCode() != 200) {
            logService.saveLog("Family not found with ID: " + id);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/updateFamily/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FamilyDTO>> updateFamily(@PathVariable Long id, @RequestBody Family familyDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Family famille = familyRepository.findById(id)
                    .orElseThrow(() -> new OurException("No family found with ID: " + id));

            logService.saveLog("Attempting to update family: " + famille.getName() + " (ID: " + id + ") by " + email);

            Response<FamilyDTO> response = familyService.updateFamily(id, familyDTO);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Family updated successfully: " + famille.getName() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to update family with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error while updating family with ID: " + id + ". Error: " + e.getMessage());
            Response<FamilyDTO> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteFamily/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteFamily(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Family family = familyRepository.findById(id)
                    .orElseThrow(() -> new OurException("No family found with ID: " + id));

            logService.saveLog("Attempting to delete family: " + family.getName() + " (ID: " + id + ") by " + email);

            Response<Void> response = familyService.deleteFamily(id);

            if (response.getStatusCode() == 200) {
                logService.saveLog("Family deleted successfully: " + family.getName() + " (ID: " + id + ") by " + email);
            } else {
                logService.saveLog("Failed to delete family with ID: " + id + ". Reason: " + response.getMessage());
            }

            return ResponseEntity.status(response.getStatusCode()).body(response);

        } catch (OurException e) {
            logService.saveLog("Error while deleting family with ID: " + id + ". Error: " + e.getMessage());
            Response<Void> errorResponse = new Response<>();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatusCode(404);
            return ResponseEntity.status(404).body(errorResponse);
        }
    }
}