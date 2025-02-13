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
    public Response<ProjectDTO> createProject(long familyId, long filiereId, Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe déjà par son nom
            if (projectRepository.existsByNameProject(project.getNameProject())) {
                throw new OurException("Project with name '" + project.getNameProject() + "' already exists.");
            }

            // Sauvegarder le nouveau projet
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);

            // Construire la réponse
            response.setStatusCode(201); // 201 Created
            response.setMessage("Project created successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error creating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<List<ProjectDTO>> getAllProjects() {
        Response<List<ProjectDTO>> response = new Response<>();
        try {
            // Récupérer tous les projets
            List<Project> projects = projectRepository.findAll();
            List<ProjectDTO> projectDTOList = Utils.mapProjectListEntityToProjectListDTO(projects);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Projects retrieved successfully");
            response.setData(projectDTOList); // Utilisation de setData pour une liste de données
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving projects: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> getProjectById(long id) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Récupérer le projet par son ID
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(project);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project retrieved successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error retrieving project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> updateProject(long projectId, Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe
            if (!projectRepository.existsById(projectId)) {
                throw new OurException("Project not found with ID: " + projectId);
            }

            // Mettre à jour le projet
            project.setId(projectId); // S'assurer que l'ID est correct
            Project updatedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project updated successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error updating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> createProject(Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe déjà par son nom
            if (projectRepository.existsByNameProject(project.getNameProject())) {
                throw new OurException("Project with name '" + project.getNameProject() + "' already exists.");
            }

            // Sauvegarder le nouveau projet
            Project savedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(savedProject);

            // Construire la réponse
            response.setStatusCode(201); // 201 Created
            response.setMessage("Project created successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(400); // 400 Bad Request
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error creating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<ProjectDTO> updateProject(Project project) {
        Response<ProjectDTO> response = new Response<>();
        try {
            // Vérifier si le projet existe
            if (!projectRepository.existsById(project.getId())) {
                throw new OurException("Project not found with ID: " + project.getId());
            }

            // Mettre à jour le projet
            Project updatedProject = projectRepository.save(project);
            ProjectDTO projectDTO = Utils.mapProjectEntityToProjectDTO(updatedProject);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project updated successfully");
            response.setData(projectDTO); // Utilisation de setData pour une réponse unique
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error updating project: " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response<Void> deleteProject(long id) {
        Response<Void> response = new Response<>();
        try {
            // Vérifier si le projet existe
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new OurException("Project not found with ID: " + id));

            // Supprimer le projet
            projectRepository.delete(project);

            // Construire la réponse
            response.setStatusCode(200); // 200 OK
            response.setMessage("Project deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(404); // 404 Not Found
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500); // 500 Internal Server Error
            response.setMessage("Error deleting project: " + e.getMessage());
        }
        return response;
    }
}