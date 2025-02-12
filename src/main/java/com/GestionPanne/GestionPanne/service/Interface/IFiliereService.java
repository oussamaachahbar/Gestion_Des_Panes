package com.GestionPanne.GestionPanne.service.Interface;
import java.util.List;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
public interface IFiliereService {
    Response createFiliere(Filiere filiereRequest);
    Response updateFiliere(long filiereId,Filiere filiereRequest);
    Response deleteFiliere(long filiereId);
    Response getAllFilieres();
    Response getFiliereById(long filiereId);
    Response getFiliereByName(String name);

}
