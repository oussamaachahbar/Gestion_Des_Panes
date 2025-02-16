package com.GestionPanne.GestionPanne.controller;


import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.service.Interface.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Family")
public class FamilyController {

    @Autowired
    private IFamilyService familyService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")

    public ResponseEntity<Response<FamilyDTO>> createFamily(@RequestBody Family familyDTO) {
        Response <FamilyDTO> response= familyService.createFamily(familyDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/all")

    public ResponseEntity<Response<FamilyDTO>> getAllFamilies() {
        Response<FamilyDTO> response= familyService.getAllFamilies();
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
        Response<FamilyDTO> response= familyService.updateFamily(id,familyDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @DeleteMapping("/deleteFamily/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<Void>> deleteFamily(@PathVariable Long id) {
        Response<Void> response= familyService.deleteFamily(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}


