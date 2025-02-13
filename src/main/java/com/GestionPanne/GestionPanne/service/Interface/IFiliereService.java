package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;

import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
public interface IFiliereService {
    Response<FiliereDTO> createFiliere(Filiere filiereRequest);
    Response<FiliereDTO> updateFiliere(long filiereId,Filiere filiereRequest);
    Response<Void> deleteFiliere(long filiereId);
    Response<List<FiliereDTO>> getAllFilieres();
    Response<FiliereDTO> getFiliereById(long filiereId);
    Response<FiliereDTO> getFiliereByName(String name);

}
