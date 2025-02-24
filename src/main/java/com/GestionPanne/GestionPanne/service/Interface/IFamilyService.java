package com.GestionPanne.GestionPanne.service.Interface;
import com.GestionPanne.GestionPanne.dto.FamilyDTO;
import com.GestionPanne.GestionPanne.entity.Family;
import com.GestionPanne.GestionPanne.dto.Response;

import java.util.List;

public interface IFamilyService {
    Response<FamilyDTO> createFamily(Family familyRequest);

    Response<List<FamilyDTO>> getAllFamilies();

    Response<FamilyDTO> getFamilyById(Long familyId);

    Response<FamilyDTO> updateFamily(Long family_id, Family familyRequest);

    Response<Void> deleteFamily(Long familyId);

}
