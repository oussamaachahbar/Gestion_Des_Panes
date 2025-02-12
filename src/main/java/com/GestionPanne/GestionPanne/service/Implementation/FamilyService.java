package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.UserDTO;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.entity.User;
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
    public Response createFamily(Family familyRequest) {
        return null;
    }

    @Override
    public Response getAllFamilies() {
        Response response = new Response();
        try {
            List<Family> families = familyRepository.findAll();
            List<FamilyDTO> familyDTOList = Utils.mapFamilyListEntityToFamilyListDTO(families);
            response.setStatusCode(200);
            response.setFamilyList(familyDTOList);
        } catch (Exception e) {
        }
        return response;
    }

    @Override
    public Response getFamilyById(Long familyId) {
        Response response = new Response();
        try {
            Family family = familyRepository.findById(familyId).orElseThrow(() -> new OurException("Family not found"));
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(family);
            response.setStatusCode(200);
            response.setFamily(familyDTO);
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
    public Response updateFamily( Family familyRequest) {
        Response response = new Response();
        try {
            if (!familyRepository.existsById(familyRequest.getId())) {
                throw new OurException("Family not found");
            }
            Family updateFamily = familyRepository.save(familyRequest);
            FamilyDTO familyDTO = Utils.mapFamilyEntityToFamilyDTO(updateFamily);
            response.setStatusCode(200);
            response.setFamily(familyDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating Family: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteFamily(Long familyId) {
        Response response = new Response();
        try {
            familyRepository.findById(familyId).orElseThrow(() -> new OurException("Family not found"));
            familyRepository.deleteById(familyId);
            response.setStatusCode(200);
            response.setMessage("Family deleted successfully");
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
