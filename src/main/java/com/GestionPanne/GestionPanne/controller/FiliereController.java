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
            response = filiereService.createFiliere(filiereDTO);

            logService.saveLog("Filière créée avec succès : " + filiereDTO.getName()+" par "+email);

        } catch (Exception e) {
            logService.saveLog("Erreur lors de la création de la filière : " + e.getMessage());

            response.setMessage("Erreur lors de la création de la filière.");
            response.setStatusCode(500);
        }

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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FiliereDTO>> updateFiliere(@PathVariable Long id, @RequestBody Filiere filiereDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Filiere filiere=filiereRepository.findById(id).orElseThrow(()->new OurException("NO FILIERE FOUND"));
        Response<FiliereDTO> response= filiereService.updateFiliere(id, filiereDTO);
        if (response.getStatusCode() == 200) {
            logService.saveLog("la filliere  avec le nom : "+filiere.getName()+" avec ID : "+id+" a été modifiée par "+email);
        } else {
            logService.saveLog("Échec de la modification du filiere avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @DeleteMapping("/deleteFiliere/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteFiliere(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Filiere filliere=filiereRepository.findById(id).orElseThrow(()->new OurException("no filliere found")) ;
        Response<Void> response= filiereService.deleteFiliere(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("la filliere  avec le nom :  "+filliere.getName()+" avec ID : "+id+" a été supprimé par "+email);
        } else {
            logService.saveLog("Échec de la delete du filiere avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}

