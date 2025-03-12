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
            response = familyService.createFamily(familyDTO);

            logService.saveLog("Famille crée avec succès : " + familyDTO.getName() +" par "+email);

        } catch (Exception e) {
            logService.saveLog("Erreur lors de la création de la famille : " + e.getMessage());

            response.setMessage("Erreur lors de la création de la famille.");
            response.setStatusCode(500);
        }

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")

    public ResponseEntity<Response<List<FamilyDTO>>> getAllFamilies() {
        Response<List<FamilyDTO>> response= familyService.getAllFamilies();
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/getFamilyById/{id}")
    public ResponseEntity<Response<FamilyDTO>> getFamilyById(@PathVariable Long id) {
        Response<FamilyDTO> response= familyService.getFamilyById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PutMapping("/updateFamily/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<FamilyDTO>> updateFamily(@PathVariable Long id, @RequestBody Family familyDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Response<FamilyDTO> response= familyService.updateFamily(id,familyDTO);
        if (response.getStatusCode() == 200) {
            logService.saveLog("la famille  avec le nom :  "+familyDTO.getName()+" a été modifié par "+email);
        } else {
            logService.saveLog("Échec de la modification du familly avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @DeleteMapping("/deleteFamily/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteFamily(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Family family=familyRepository.findById(id).orElseThrow(()->new OurException("no family found")) ;
        Response<Void> response= familyService.deleteFamily(id);
        if (response.getStatusCode() == 200) {
            logService.saveLog("la famille  avec le nom : "+family.getName()+" a été supprimé par "+email);
        } else {
            logService.saveLog("Échec de la delete du familly avec ID: "+id );
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}


