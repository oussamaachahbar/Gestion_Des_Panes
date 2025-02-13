package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
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
    public Response<List<FiliereDTO>> getAllFilieres() {
        Response<List<FiliereDTO>> response = new Response<>();
        try {
            List<Filiere> filieres = filiereRepository.findAll();
            List<FiliereDTO> filiereDTOList = Utils.mapFiliereListEntityToFiliereListDTO(filieres);
            response.setStatusCode(200); // 200 OK
            response.setMessage("Filieres retrieved successfully");
            response.setData(filiereDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving filières: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FiliereDTO> getFiliereById(long filiereId) {
        Response<FiliereDTO> response = new Response<>();
        try {
            Filiere filiere = filiereRepository.findById(filiereId)
                    .orElseThrow(() -> new OurException("Filiere not found with ID: " + filiereId));
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(filiere);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Filiere retrieved successfully");
            response.setData(filiereDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving filiere: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FiliereDTO> getFiliereByName(String name) {
        Response<FiliereDTO> response = new Response<>();
        try {
            Filiere filiere = filiereRepository.findByName(name)
                    .orElseThrow(() -> new OurException("Filiere not found with name: " + name));
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(filiere);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Filiere retrieved successfully");
            response.setData(filiereDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving filiere: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FiliereDTO> createFiliere(Filiere filiere) {
        Response<FiliereDTO> response = new Response<>();
        try {
            if (filiereRepository.existsByName(filiere.getName())) {
                throw new OurException("Filiere with name '" + filiere.getName() + "' already exists.");
            }
            Filiere savedFiliere = filiereRepository.save(filiere);
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(savedFiliere);

            response.setStatusCode(201); // 201 Created
            response.setMessage("Filiere created successfully");
            response.setData(filiereDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error adding filière: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<FiliereDTO> updateFiliere(long filiereId, Filiere filiereRequest) {
        Response<FiliereDTO> response = new Response<>();
        try {
            // Vérifier si la filière existe
            if (!filiereRepository.existsById(filiereId)) {
                throw new OurException("Filiere not found with ID: " + filiereId);
            }

            filiereRequest.setId(filiereId); // S'assurer que l'ID est correct
            Filiere updatedFiliere = filiereRepository.save(filiereRequest);
            FiliereDTO filiereDTO = Utils.mapFiliereEntityToFiliereDTO(updatedFiliere);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Filiere updated successfully");
            response.setData(filiereDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error updating Filiere: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Void> deleteFiliere(long id) {
        Response<Void> response = new Response<>();
        try {
            Filiere filiere = filiereRepository.findById(id)
                    .orElseThrow(() -> new OurException("Filière not found with ID: " + id));

            filiereRepository.delete(filiere);

            response.setStatusCode(200); // 200 OK
            response.setMessage("Filière deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting filière: " + e.getMessage());
        }
        return response;
    }
}