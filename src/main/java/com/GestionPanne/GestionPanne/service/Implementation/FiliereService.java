package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.FiliereRepository;
import com.GestionPanne.GestionPanne.service.Interface.IFiliereService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FiliereService implements IFiliereService {
    @Autowired
    private FiliereRepository filiereRepository;

    @Override
    public Response getAllFilieres() {
        Response response = new Response();
        try {
            List<Filiere> filieres = filiereRepository.findAll();
            List<FiliereDTO> filiereDTOList = Utils.mapFiliereListEntityToFiliereListDTO(filieres);
            response.setStatusCode(200);
            response.setFiliereList(filiereDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving filières: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getFiliereById(long filiereId) {
        Response response = new Response();
        try {
            Filiere filiere = filiereRepository.findById(filiereId).orElseThrow(() -> new OurException("Filiere not found"));
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(filiere);
            response.setStatusCode(200);
            response.setFiliere(filiereDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving filiere: " + e.getMessage());
        }
        return response;    }

    @Override
    public Response getFiliereByName(String name) {
        return null;
    }

    @Override
    public Response createFiliere(Filiere filiere) {
        Response response = new Response();
        try {
            Filiere savedFiliere = filiereRepository.save(filiere);
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(savedFiliere);
            response.setStatusCode(200);
            response.setFiliere(filiereDTO);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding filière: " + e.getMessage());
        }
        return response;
    }



    @Override
    public Response updateFiliere(long filiereId, Filiere filiereRequest) {
        Response response = new Response();
        try {
            if (!filiereRepository.existsById(filiereRequest.getId())) {
                throw new OurException("Filiere not found");
            }
            Filiere updateFiliere = filiereRepository.save(filiereRequest);
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(updateFiliere);
            response.setStatusCode(200);
            response.setFiliere(filiereDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating Filiere: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteFiliere(long id) {
        Response response = new Response();
        try {
            if (!filiereRepository.existsById(id)) {
                throw new OurException("Filière not found");
            }
            filiereRepository.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("Filière deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting filière: " + e.getMessage());
        }
        return response;
    }
}

