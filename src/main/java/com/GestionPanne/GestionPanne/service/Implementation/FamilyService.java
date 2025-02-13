package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FamilyRepository;
import com.GestionPanne.GestionPanne.service.Interface.IFamilyService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyService implements IFamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Override
    public Response<FamilyDTO> createFamily(Family familyRequest) {
        Response<FamilyDTO> response = new Response<>();
        try {
            if (familyRepository.existsByName(familyRequest.getName())) {
                throw new OurException("Family with name '" + familyRequest.getName() + "' already exists.");
            }

            Family savedFamily = familyRepository.save(familyRequest);
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(savedFamily);

            response.setStatusCode(201);
            response.setMessage("Family created successfully");
            response.setData(familyDTO);
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error creating family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FamilyDTO> getAllFamilies() {
        Response<FamilyDTO> response = new Response<>();
        try {
            // Récupérer toutes les familles
            List<Family> families = familyRepository.findAll();
            List<FamilyDTO> familyDTOList = Utils.mapFamilyListEntityToFamilyListDTO(families);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Families retrieved successfully");
            response.setDataList(familyDTOList); // Utilisation de setDataList pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving families: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FamilyDTO> getFamilyById(Long familyId) {
        Response<FamilyDTO> response = new Response<>();
        try {
            // Récupérer la famille par son ID
            Family family = familyRepository.findById(familyId)
                    .orElseThrow(() -> new OurException("Family not found with ID: " + familyId));
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(family);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Family retrieved successfully");
            response.setData(familyDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FamilyDTO> updateFamily(Family familyRequest) {
        Response<FamilyDTO> response = new Response<>();
        try {
            // Vérifier si la famille existe
            if (!familyRepository.existsById(familyRequest.getId())) {
                throw new OurException("Family not found with ID: " + familyRequest.getId());
            }

            // Mettre à jour la famille
            Family updatedFamily = familyRepository.save(familyRequest);
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(updatedFamily);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Family updated successfully");
            response.setData(familyDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error updating family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Void> deleteFamily(Long familyId) {
        Response<Void> response = new Response<>();
        try {
            Family family = familyRepository.findById(familyId)
                    .orElseThrow(() -> new OurException("Family not found with ID: " + familyId));

            familyRepository.delete(family);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Family deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting family: " + e.getMessage());
        }
        return response;
    }
}