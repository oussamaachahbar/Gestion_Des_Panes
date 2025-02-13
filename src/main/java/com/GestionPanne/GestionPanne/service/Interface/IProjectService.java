package com.GestionPanne.GestionPanne.service.Interface;
import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Project;

import java.util.List;

public interface IProjectService {
    Response<ProjectDTO> createProject(long familyId, long filiereId, Project project);
    Response<List<ProjectDTO>> getAllProjects();
    Response<ProjectDTO> getProjectById(long projectId);
    Response<ProjectDTO> updateProject(long projectId,Project project);

    Response<ProjectDTO> createProject(Project project);

    Response<ProjectDTO> updateProject(Project project);

    Response<Void> deleteProject(long projectId);

}
