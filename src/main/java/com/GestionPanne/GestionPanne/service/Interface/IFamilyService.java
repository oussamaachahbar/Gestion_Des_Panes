package com.GestionPanne.GestionPanne.service.Interface;
import com.GestionPanne.GestionPanne.service.*;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.dto.Response;
public interface IFamilyService {
    Response createFamily(Family familyRequest);

    Response getAllFamilies();

    Response getFamilyById(Long familyId);

    Response updateFamily( Family familyRequest);

    Response deleteFamily(Long familyId);

}
