package com.GestionPanne.GestionPanne.service.Implementation;
import com.GestionPanne.GestionPanne.dto.FiliereDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.dto.logDTO;
import com.GestionPanne.GestionPanne.entity.Filiere;
import com.GestionPanne.GestionPanne.entity.Log;
import com.GestionPanne.GestionPanne.repo.LogRepository;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void saveLog(String action) {
        Log log = new Log(action);
        logRepository.save(log);
    }

    public Response<List<logDTO>> getAllLog() {
        Response<List<logDTO>> response = new Response<>();
        try {
            List<Log> logs = logRepository.findAll();
            List<logDTO> logDTOList = Utils.mapLogListEntityToLogListDTO(logs);
            response.setStatusCode(200); // 200 OK
            response.setMessage("Logs retrieved successfully");
            response.setData(logDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving Logs: " + e.getMessage());
        }
        return response;
    }
}
