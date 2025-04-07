package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FamilyRepository;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.service.Interface.IFamilyService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamilyService implements IFamilyService {

    @Autowired
    private FamilyRepository familyRepository;
    @Autowired
    private ProjectRepository projectRepository;

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
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error creating family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<FamilyDTO>> getAllFamilies() {
        Response<List<FamilyDTO>> response = new Response<>();
        try {
            List<Family> families = familyRepository.findAll();
            List<FamilyDTO> familyDTOList = Utils.mapFamilyListEntityToFamilyListDTO(families);

            response.setStatusCode(200);
            response.setMessage("Families retrieved successfully");
            response.setData(familyDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving families: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FamilyDTO> getFamilyById(Long familyId) {
        Response<FamilyDTO> response = new Response<>();
        try {
            Family family = familyRepository.findById(familyId)
                    .orElseThrow(() -> new OurException("Family not found with ID: " + familyId));
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(family);

            response.setStatusCode(200);
            response.setMessage("Family retrieved successfully");
            response.setData(familyDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FamilyDTO> updateFamily(Long familyid,Family familyRequest) {
        Response<FamilyDTO> response = new Response<>();
        try {
            if (!familyRepository.existsById(familyid)) {
                throw new OurException("Family not found with ID: " + familyRequest.getId());
            }
            Optional<Family> familleOpt = familyRepository.findById(familyid);
            Family existingFamille= familleOpt.get();
            existingFamille.setName(familyRequest.getName());
            Family updatedFamily = familyRepository.save(existingFamille);
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(updatedFamily);

            response.setStatusCode(200);
            response.setMessage("Family updated successfully");
            response.setData(familyDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
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

            List<Project> projects = projectRepository.findByFamilyId(familyId);

            for (Project project : projects) {
                project.setFamily(null);
                projectRepository.save(project);
            }
            familyRepository.delete(family);

            response.setStatusCode(200);
            response.setMessage("Family deleted successfully, projects updated");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting family: " + e.getMessage());
        }
        return response;
    }

}