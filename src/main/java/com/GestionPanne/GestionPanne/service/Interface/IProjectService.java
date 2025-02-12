package com.GestionPanne.GestionPanne.service.Interface;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Project;
public interface IProjectService {
    Response createProject(long familyId,long filiereId,Project project);
    Response getAllProjects();
    Response getProjectById(long projectId);
    Response updateProject(long projectId,Project project);
    Response deleteProject(long projectId);

}
