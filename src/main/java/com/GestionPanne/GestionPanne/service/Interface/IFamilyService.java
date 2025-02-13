package com.GestionPanne.GestionPanne.service.Interface;
import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.service.*;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.dto.Response;
public interface IFamilyService {
    Response<FamilyDTO> createFamily(Family familyRequest);

    Response<FamilyDTO> getAllFamilies();

    Response<FamilyDTO> getFamilyById(Long familyId);

    Response<FamilyDTO> updateFamily( Family familyRequest);

    Response<Void> deleteFamily(Long familyId);

}
