package com.GestionPanne.GestionPanne.controller;


import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.service.Interface.IFamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/families")
public class FamilyController {

    @Autowired
    private IFamilyService familyService;

    @PostMapping
    public ResponseEntity<Response<FamilyDTO>> createFamily(@RequestBody Family familyDTO) {
        Response <FamilyDTO> response= familyService.createFamily(familyDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping
    public ResponseEntity<Response<FamilyDTO>> getAllFamilies() {
        Response<FamilyDTO> response= familyService.getAllFamilies();
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<FamilyDTO>> getFamilyById(@PathVariable Long id) {
        Response<FamilyDTO> response= familyService.getFamilyById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<FamilyDTO>> updateFamily( @RequestBody Family familyDTO) {
        Response<FamilyDTO> response= familyService.updateFamily(familyDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteFamily(@PathVariable Long id) {
        Response<Void> response= familyService.deleteFamily(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}


