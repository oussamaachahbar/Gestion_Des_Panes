package com.GestionPanne.GestionPanne.service.Implementation;

import com.GestionPanne.GestionPanne.dto.ProjectDTO;
import com.GestionPanne.GestionPanne.dto.Response;
import com.GestionPanne.GestionPanne.entity.Project;
import com.GestionPanne.GestionPanne.exception.OurException;
import com.GestionPanne.GestionPanne.repo.ProjectRepository;
import com.GestionPanne.GestionPanne.service.Interface.IProjectService;
import com.GestionPanne.GestionPanne.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService implements IProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Response createProject(long familyId, long filiereId, Project project) {
        return null;
    }

    @Override
    public Response getAllProjects() {
        Response response = new Response();
        try {
            List<Project> projects = projectRepository.findAll();
            List<ProjectDTO> projectDTOList = Utils.mapProjectListEntityToProjectListDTO(projects);
            response.setStatusCode(200);
            response.setProjectList(projectDTOList);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving projects: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getProjectById(long id) {
        Response response = new Response();
        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found"));
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(project);
            response.setStatusCode(200);
            response.setProject(projectDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateProject(long projectId, Project project) {
        Response response = new Response();
        try {
            if (!projectRepository.existsById(projectId)) {
                throw new OurException("Project not found");
            }
            Project updatedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);
            response.setStatusCode(200);
            response.setProject(projectDTO);
        } catch (OurException e) {

        }
        return response;
    }

    @Override
    public Response createProject(Project project) {
        Response response = new Response();
        try {
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);
            response.setStatusCode(200);
            response.setProject(projectDTO);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error adding project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response updateProject(Project project) {
        Response response = new Response();
        try {
            if (!projectRepository.existsById(project.getId())) {
                throw new OurException("Project not found");
            }
            Project updatedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);
            response.setStatusCode(200);
            response.setProject(projectDTO);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error updating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteProject(long id) {
        Response response = new Response();
        try {
            if (!projectRepository.existsById(id)) {
                throw new OurException("Project not found");
            }
            projectRepository.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("Project deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error deleting project: " + e.getMessage());
        }
        return response;
    }
}
